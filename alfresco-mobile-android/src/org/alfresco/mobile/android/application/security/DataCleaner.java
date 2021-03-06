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
package org.alfresco.mobile.android.application.security;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.mobile.android.application.R;
import org.alfresco.mobile.android.application.accounts.AccountDAO;
import org.alfresco.mobile.android.application.utils.SessionUtils;
import org.alfresco.mobile.android.ui.manager.MessengerManager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class DataCleaner extends AsyncTask<String, Integer, Boolean>
{
    private static final String TAG = "DataCleaner";

    private List<File> listingFiles = new ArrayList<File>();

    private Activity activity;

    public DataCleaner(Activity activity)
    {
        super();
        this.activity = activity;
    }

    @Override
    protected Boolean doInBackground(String... params)
    {
        try
        {
            //Remove preferences
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
            Editor editor = sharedPref.edit();
            editor.clear();
            editor.commit();
            
            //Remove Account
            AccountDAO accountDao = new AccountDAO(activity, SessionUtils.getDataBaseManager(activity).getWriteDb());
            accountDao.clear();
            
            //Find folders
            File cache = activity.getCacheDir();
            File folder = activity.getExternalFilesDir(null);
            
            listingFiles.add(cache);
            listingFiles.add(folder);
            
            //Remove Files/folders
            for (File file : listingFiles)
            {
                if (file.exists())
                {
                    if (file.isDirectory())
                    {
                        recursiveDelete(file);
                    }
                    else
                    {
                        file.delete();
                    }
                }
            }
            return true;
        }
        catch (Exception fle)
        {
            Log.e(TAG, Log.getStackTraceString(fle));
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean statut)
    {
        if (statut)
        {
            MessengerManager.showLongToast(activity, activity.getString(R.string.passcode_erase_data_complete));
            activity.setResult(Activity.RESULT_CANCELED);
            activity.finish();
        }
    }

    private boolean recursiveDelete(File file)
    {
        File[] files = file.listFiles();
        File childFile;
        if (files != null)
        {
            for (int x = 0; x < files.length; x++)
            {
                childFile = files[x];
                if (childFile.isDirectory())
                {
                    if (!recursiveDelete(childFile)) { return false; }
                }
                else
                {
                    if (!childFile.delete()) { return false; }
                }
            }
        }
        if (!file.delete()) { return false; }

        return true;
    }
}
