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
package org.alfresco.mobile.android.application.accounts;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.mobile.android.application.database.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Manage Account database table.
 * 
 * @author Jean Marie Pascal
 */
public class AccountDAO extends DAO<Account>
{

    private final SQLiteDatabase db;

    public AccountDAO(Context context, SQLiteDatabase db)
    {
        super(context);
        this.db = db;
    }

    public long insert(String name, String url, String username, String pass, String workspace, Integer type,
            String accessToken, String refreshToken, int isPaid)
    {
        ContentValues insertValues = createContentValues(name, url, username, pass, workspace, type, null, accessToken,
                refreshToken, isPaid);

        return db.insert(AccountSchema.TABLENAME, null, insertValues);
    }

    public long insert(String name, String url, String username, String pass, String workspace, Integer type,
            String activation, String accessToken, String refreshToken, int isPaid)
    {
        ContentValues insertValues = createContentValues(name, url, username, pass, workspace, type, activation,
                accessToken, refreshToken, isPaid);

        return db.insert(AccountSchema.TABLENAME, null, insertValues);
    }

    public boolean update(long id, String name, String url, String username, String pass, String workspace,
            Integer type, String activation, String accessToken, String refreshToken, int isPaid)
    {
        ContentValues updateValues = createContentValues(name, url, username, pass, workspace, type, activation,
                accessToken, refreshToken, isPaid);

        return db.update(AccountSchema.TABLENAME, updateValues, AccountSchema.COLUMN_ID + "=" + id, null) > 0;
    }

    public boolean delete(long id)
    {
        return db.delete(AccountSchema.TABLENAME, AccountSchema.COLUMN_ID + "=" + id, null) > 0;
    }

    /**
     * Use with extreme caution ! This method clear all accounts from the
     * accounts table. It's like reset.
     */
    public boolean clear()
    {
        return db.delete(AccountSchema.TABLENAME, "1", null) > 0;
    }

    public List<Account> findAll()
    {
        Cursor c = db.query(AccountSchema.TABLENAME, new String[] { AccountSchema.COLUMN_ID, AccountSchema.COLUMN_NAME,
                AccountSchema.COLUMN_URL, AccountSchema.COLUMN_USERNAME, AccountSchema.COLUMN_PASSWORD,
                AccountSchema.COLUMN_REPOSITORY_ID, AccountSchema.COLUMN_REPOSITORY_TYPE,
                AccountSchema.COLUMN_ACTIVATION, AccountSchema.COLUMN_ACCESS_TOKEN, AccountSchema.COLUMN_REFRESH_TOKEN,
                AccountSchema.COLUMN_IS_PAID_ACCOUNT }, null, null, null, null, AccountSchema.COLUMN_ID + " ASC");
        return cursorToAccounts(c);
    }

    public Account findById(long id)
    {
        Cursor c = db.query(AccountSchema.TABLENAME, null, AccountSchema.COLUMN_ID + " like " + id, null, null, null,
                null);

        if (c != null)
        {
            c.moveToFirst();
            return cursorToAccount(c);
        }
        return null;
    }

    private ContentValues createContentValues(String name, String url, String username, String pass, String workspace,
            Integer type, String activation, String accessToken, String refreshToken, int isPaidAccount)
    {
        ContentValues updateValues = new ContentValues();

        updateValues.put(AccountSchema.COLUMN_NAME, name);
        updateValues.put(AccountSchema.COLUMN_URL, url);
        updateValues.put(AccountSchema.COLUMN_USERNAME, username);
        updateValues.put(AccountSchema.COLUMN_PASSWORD, pass);
        updateValues.put(AccountSchema.COLUMN_REPOSITORY_ID, workspace);
        updateValues.put(AccountSchema.COLUMN_REPOSITORY_TYPE, type);
        updateValues.put(AccountSchema.COLUMN_ACTIVATION, activation);
        updateValues.put(AccountSchema.COLUMN_ACCESS_TOKEN, accessToken);
        updateValues.put(AccountSchema.COLUMN_REFRESH_TOKEN, refreshToken);
        updateValues.put(AccountSchema.COLUMN_IS_PAID_ACCOUNT, isPaidAccount);
        return updateValues;
    }

    private ArrayList<Account> cursorToAccounts(Cursor c)
    {
        if (c.getCount() == 0) { return new ArrayList<Account>(); }

        ArrayList<Account> accounts = new ArrayList<Account>(c.getCount());
        c.moveToFirst();

        do
        {
            Account account = createAccountFromCursor(c);
            accounts.add(account);
        } while (c.moveToNext());
        c.close();
        return accounts;
    }

    private Account createAccountFromCursor(Cursor c)
    {
        Account account = new Account(c.getInt(AccountSchema.COLUMN_ID_ID), c.getString(AccountSchema.COLUMN_NAME_ID),
                c.getString(AccountSchema.COLUMN_URL_ID), c.getString(AccountSchema.COLUMN_USERNAME_ID),
                c.getString(AccountSchema.COLUMN_PASSWORD_ID), c.getString(AccountSchema.COLUMN_REPOSITORY_ID_ID),
                c.getInt(AccountSchema.COLUMN_REPOSITORY_TYPE_ID), c.getString(AccountSchema.COLUMN_ACTIVATION_ID),
                c.getString(AccountSchema.COLUMN_ACCESS_TOKEN_ID), c.getString(AccountSchema.COLUMN_REFRESH_TOKEN_ID),
                c.getInt(AccountSchema.COLUMN_IS_PAID_ACCOUNT_ID));
        return account;
    }

    private Account cursorToAccount(Cursor c)
    {
        if (c.getCount() == 0) { return null; }
        Account account = createAccountFromCursor(c);
        c.close();
        return account;
    }

}
