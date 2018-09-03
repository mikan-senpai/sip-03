/*
SelectableHelper.java
Copyright (C) 2017  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package org.linphone.ui;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.linphone.R;
import org.linphone.activities.LinphoneActivity;

public class SelectableHelper {
    private ImageView mEditButton, mSelectAllButton, mDeselectAllButton, mDeleteSelectionButton, mCancelButton;
    private LinearLayout mEditTopBar, mTopBar;
    private SelectableAdapter<RecyclerView.ViewHolder> mAdapter;
    private DeleteListener mDeleteListener;
    private Context mContext;
    private int mDialogDeleteMessageResourceId;

    public void setDialogMessage(int id) {
        mDialogDeleteMessageResourceId = id;
    }

    public interface DeleteListener {
        void onDeleteSelection(Object[] objectsToDelete);
    }

    public SelectableHelper(View view, DeleteListener listener) {
        mContext = view.getContext();
        mDeleteListener = listener;

        //Define XML layout items used for selection mode

        mEditTopBar = view.findViewById(R.id.edit_list);
        mTopBar = view.findViewById(R.id.top_bar);

        mCancelButton = view.findViewById(R.id.cancel);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitEditionMode();
            }
        });

        mEditButton = view.findViewById(R.id.edit);

        //Set visibility behaviour on click on edit button + enter Edition Mode
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.getItemCount() > 0) {
                    enterEditionMode();
                    mTopBar.setVisibility(View.GONE);
                    mEditTopBar.setVisibility(View.VISIBLE);
                }
            }
        });

        mSelectAllButton = view.findViewById(R.id.select_all);
        mSelectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.selectAll();
            }
        });

        mDeselectAllButton = view.findViewById(R.id.deselect_all);
        mDeselectAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.deselectAll();
            }
        });

        mDeleteSelectionButton = view.findViewById(R.id.delete);
        mDeleteSelectionButton.setEnabled(false);

        //Display confirmation for deletion
        mDeleteSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = LinphoneActivity.instance().displayDialog(mContext.getString(mDialogDeleteMessageResourceId));
                Button delete = dialog.findViewById(R.id.delete_button);
                Button cancel = dialog.findViewById(R.id.cancel);

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDeleteListener.onDeleteSelection(getSelectedObjects());
                        dialog.dismiss();
                        quitEditionMode();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        quitEditionMode();
                    }
                });
                dialog.show();
            }
        });

        mDialogDeleteMessageResourceId = R.string.delete_text;
    }

    public void setAdapter(SelectableAdapter adapter) {
        mAdapter=adapter;
    }

    public void updateSelectionButtons(boolean isSelectionEmpty, boolean isSelectionFull) {
        //Delete button availability
        if (isSelectionEmpty) {
            mDeleteSelectionButton.setEnabled(false);
        } else {
            mDeleteSelectionButton.setEnabled(true);
        }

        //(De)select button visibility
        if (isSelectionFull) {
            mSelectAllButton.setVisibility(View.GONE);
            mDeselectAllButton.setVisibility(View.VISIBLE);
        } else {
            mSelectAllButton.setVisibility(View.VISIBLE);
            mDeselectAllButton.setVisibility(View.GONE);
        }
    }

    public void quitEditionMode() {
        mAdapter.enableEdition(false);
        mTopBar.setVisibility(View.VISIBLE);
        mEditTopBar.setVisibility(View.GONE);
        mDeleteSelectionButton.setEnabled(false);
        mSelectAllButton.setVisibility(View.GONE);
        mDeselectAllButton.setVisibility(View.VISIBLE);
    }

    public void enterEditionMode() {
        mAdapter.enableEdition(true);
        mTopBar.setVisibility(View.GONE);
        mEditTopBar.setVisibility(View.VISIBLE);
        mDeleteSelectionButton.setEnabled(false);
        mSelectAllButton.setVisibility(View.VISIBLE);
        mDeselectAllButton.setVisibility(View.GONE);
    }

    private Object[] getSelectedObjects() {
        Object objects[] = new Object[mAdapter.getSelectedItemCount()];
        int index = 0;
        for (Integer i : mAdapter.getSelectedItems()) {
            objects[index] = mAdapter.getItem(i);
            index++;
        }
        return objects;
    }
}