/*******************************************************************************
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * 
 * This file is part of Alfresco Mobile for Android.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.alfresco.mobile.android.application.fragments.browser.local;

import java.io.File;
import java.util.ArrayList;

import org.alfresco.mobile.android.application.MenuActionItem;
import org.alfresco.mobile.android.application.R;
import org.alfresco.mobile.android.application.intent.IntentIntegrator;
import org.alfresco.mobile.android.application.manager.ActionManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

/**
 * Manage all local file actions like a sdcard file manager.
 * 
 * @author Jean Marie Pascal
 */
public class FileActions implements ActionMode.Callback
{

    private ArrayList<File> files = new ArrayList<File>();

    private onFinishModeListerner mListener;

    private ActionMode mode;

    private Activity activity;

    private Fragment fragment;

    public FileActions(Fragment f, File file)
    {
        this.fragment = f;
        this.activity = f.getActivity();
        files.add(file);
    }

    // ///////////////////////////////////////////////////////////////////////////////////
    // ACTION MODE
    // ///////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item)
    {
        switch (item.getItemId())
        {
            case MenuActionItem.MENU_DELETE:
                delete(activity, fragment, files.get(0));
                mode.finish();
                files.clear();
                return true;
            case MenuActionItem.MENU_EDIT:
                edit(activity, fragment, files.get(0));
                mode.finish();
                files.clear();
                return true;
            case MenuActionItem.MENU_SHARE:
                share(fragment, files.get(0));
                mode.finish();
                files.clear();
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu)
    {
        return true;
    }

    private void getMenu(Menu menu, File file)
    {
        menu.clear();

        MenuItem mi;

        mi = menu.add(Menu.NONE, MenuActionItem.MENU_SHARE, Menu.FIRST + MenuActionItem.MENU_SHARE, R.string.share);
        mi.setIcon(R.drawable.ic_share);
        mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        mi = menu.add(Menu.NONE, MenuActionItem.MENU_EDIT, Menu.FIRST + MenuActionItem.MENU_EDIT, R.string.edit);
        mi.setIcon(R.drawable.ic_edit);
        mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        mi = menu.add(Menu.NONE, MenuActionItem.MENU_DELETE, Menu.FIRST + MenuActionItem.MENU_DELETE, R.string.delete);
        mi.setIcon(R.drawable.ic_delete);
        mi.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

    }

    @Override
    public void onDestroyActionMode(ActionMode mode)
    {
        mListener.onFinish();
        files.clear();
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu)
    {
        this.mode = mode;
        getMenu(menu, files.get(0));
        return false;
    }

    public void addFile(File n)
    {
        files.clear();
        files.add(n);
        mode.setTitle(n.getName());
        mode.invalidate();
    }

    public void setOnFinishModeListerner(onFinishModeListerner mListener)
    {
        this.mListener = mListener;
    }

    public void finish()
    {
        mode.finish();
    }

    // ///////////////////////////////////////////////////////////////////////////////////
    // LISTENER
    // ///////////////////////////////////////////////////////////////////////////////////
    public interface onFinishModeListerner
    {
        public void onFinish();
    }

    // ///////////////////////////////////////////////////////////////////////////////////
    // ACTIONS
    // ///////////////////////////////////////////////////////////////////////////////////
    public static void share(final Fragment fr, final File file)
    {
        ActionManager.actionShareContent(fr, file);
    }

    public static void edit(final Activity activity, final Fragment f, final File file)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.action_rename);
        builder.setMessage(String.format(activity.getResources().getString(R.string.action_rename_desc), file.getName()));
        // Set an EditText view to get user input
        final EditText input = new EditText(activity);
        builder.setView(input);

        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int item)
            {
                String value = input.getText().toString();
                file.renameTo(new File(file.getParent(), value));
                ActionManager.actionRefresh(f, IntentIntegrator.CATEGORY_REFRESH_OTHERS, IntentIntegrator.FILE_TYPE,
                        null);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int item)
            {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void delete(final Activity activity, final Fragment f, final File file)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.delete);
        builder.setMessage(String.format(activity.getResources().getString(R.string.delete_description),file.getName()));
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int item)
            {
                Log.d("Delete File", file.getName());
                file.delete();
                ActionManager.actionRefresh(f, IntentIntegrator.CATEGORY_REFRESH_OTHERS, IntentIntegrator.FILE_TYPE,
                        null);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int item)
            {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
