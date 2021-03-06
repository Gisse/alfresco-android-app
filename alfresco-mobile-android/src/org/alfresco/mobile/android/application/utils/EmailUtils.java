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
package org.alfresco.mobile.android.application.utils;

import java.io.IOException;

import org.alfresco.mobile.android.application.R;
import org.alfresco.mobile.android.application.preferences.GeneralPreferences;
import org.alfresco.mobile.android.ui.manager.MessengerManager;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;

public final class EmailUtils
{
    private EmailUtils()
    {
    }

    public static final String TAG = "EmailUtils";

    public static boolean createMailWithAttachment(Fragment fr, String subject, String content, Uri attachment,
            int requestCode)
    {
        try
        {
            if (CipherUtils.isEncrypted(fr.getActivity(), attachment.getPath(), true)
                    && CipherUtils.decryptFile(fr.getActivity(), attachment.getPath()))
            {
                PreferenceManager.getDefaultSharedPreferences(fr.getActivity()).edit()
                        .putString(GeneralPreferences.REQUIRES_ENCRYPT, attachment.getPath()).commit();
            }

            Intent i = new Intent(Intent.ACTION_SEND);
            i.putExtra(Intent.EXTRA_SUBJECT, subject);
            i.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(content));
            i.putExtra(Intent.EXTRA_STREAM, attachment);
            i.setType("text/plain");
            fr.startActivityForResult(Intent.createChooser(i, fr.getString(R.string.send_email)), requestCode);

            return true;
        }
        catch (IOException e)
        {
            MessengerManager.showToast(fr.getActivity(), R.string.decryption_failed);
            Log.d(TAG, Log.getStackTraceString(e));
        }
        catch (Exception e)
        {
            MessengerManager.showToast(fr.getActivity(), R.string.decryption_failed);
            Log.d(TAG, Log.getStackTraceString(e));
        }

        return false;
    }

    public static boolean createMailWithLink(Context c, String subject, String content, Uri link)
    {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, content);
        i.setType("text/plain");
        c.startActivity(Intent.createChooser(i, String.format(c.getString(R.string.send_email), link.toString())));

        return true;
    }
}
