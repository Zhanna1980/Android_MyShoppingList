package com.example.zhannalibman.myshoppinglist;


import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhannalibman on 05/01/2017.
 */

public class MoveCopyDialogFragment extends DialogFragment {
    //Selected item
    ItemPosition itemPosition;

    boolean shouldCopyItem;

    private OnFinishMoveCopyDialogListener onFinishMoveCopyDialogListener;

    public void setOnFinishMoveCopyDialogListener(OnFinishMoveCopyDialogListener onFinishMoveCopyDialogListener) {
        this.onFinishMoveCopyDialogListener = onFinishMoveCopyDialogListener;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_move_copy, null);
        ListView moveCopyToList = (ListView) view.findViewById(R.id.moveCopyToList);
        shouldCopyItem = getArguments().getBoolean("shouldCopyItem");
        itemPosition = (ItemPosition) getArguments().getSerializable("itemPosition");
        final Item item = itemPosition.getItem();
        final ShoppingList currentList = CurrentState.getInstance().listList.get(itemPosition.shoppingListIndexInListList);
        String copyOrMove = shouldCopyItem ? getString(R.string.copy) : getString(R.string.move);
        String title = copyOrMove + " " + item.getName() + " " + getString(R.string.to);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setView(view);
        final List<ShoppingList> listForDisplayingInDialog = getListForDisplayingInDialog(itemPosition.shoppingListIndexInListList);
        ArrayAdapter<ShoppingList> moveCopyToListAdapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
                        listForDisplayingInDialog);
        moveCopyToList.setAdapter(moveCopyToListAdapter);
        moveCopyToList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                  @Override
                                                  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                      ShoppingList destinationList = listForDisplayingInDialog.get(position);
                                                      if (!destinationList.containsItem(item.getName())) {
                                                          destinationList.itemList.add(0, item);
                                                          if (!shouldCopyItem) {
                                                              if (itemPosition.isSelectedItemInItemList) {
                                                                  currentList.itemList.remove(itemPosition.positionInSectionList);
                                                              } else {
                                                                  currentList.inCart.remove(itemPosition.positionInSectionList);
                                                              }
                                                          }
                                                          doneMoveCopyItem(shouldCopyItem);
                                                      } else {
                                                          Toast.makeText(getActivity(), getString(R.string.already_in_list), Toast.LENGTH_SHORT).show();
                                                          dismiss();
                                                      }
                                                  }
                                              }
        );
        return builder.create();
    }

    /**
     * Returns the list without the current list with item that is being moved or copied
     */
    private List<ShoppingList> getListForDisplayingInDialog(int currentListIndex) {
        List<ShoppingList> listForDisplayingInDialog = new LinkedList<>();
        List<ShoppingList> listList = CurrentState.getInstance().listList;
        for (int i = 0; i < listList.size(); i++) {
            if (i != currentListIndex) {
                listForDisplayingInDialog.add(listList.get(i));
            }
        }
        return listForDisplayingInDialog;
    }

    private void doneMoveCopyItem(boolean shouldCopyItem) {
        if (onFinishMoveCopyDialogListener != null)
            onFinishMoveCopyDialogListener.onFinishMoveCopyItem(shouldCopyItem);
        dismiss();
    }

    public interface OnFinishMoveCopyDialogListener {
        void onFinishMoveCopyItem(boolean isItemCopied);
    }

}
