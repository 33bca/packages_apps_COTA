/*
 * Copyright 2014-2015 ParanoidAndroid Project
 *
 * This file is part of CypherOS OTA.
 *
 * CypherOS OTA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CypherOS OTA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CypherOS OTA.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cypher.cota.updater.server;

import android.util.Log;
import com.cypher.cota.updater.Server;
import com.cypher.cota.updater.UpdatePackage;
import com.cypher.cota.updater.Updater;
import com.cypher.cota.utils.Constants;
import com.cypher.cota.utils.Version;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CypherServer implements Server {
	
	private static final String TAG = Constants.BASE_TAG + "CypherServer";

    private static final String URL = "https://raw.githubusercontent.com/33bca/AOSCPHub-API/master/%s";

    private String mDevice = null;
    private String mError = null;
    private Version mVersion;

    @Override
    public String getUrl(String device, Version version) {
        mDevice = device;
        mVersion = version;
        return String.format(URL, device);
    }

    @Override
    public List<Updater.PackageInfo> createPackageInfoList(JSONObject response) throws Exception {
        mError = null;
        List<Updater.PackageInfo> list = new ArrayList<>();
        mError = response.optString("error");
        if (mError == null || mError.isEmpty()) {
            JSONArray updates = response.getJSONArray("updates");
            for (int i = updates.length() - 1; i >= 0; i--) {
                JSONObject file = updates.getJSONObject(i);
                String filename = file.getString("name");
                String versionString  = file.getString("version");
                String dateString     = file.getString("build");

                if (Constants.DEBUG) Log.d(TAG, "version from server: " + versionString + " " + dateString);
                Version version = new Version(versionString, dateString);
                if (Version.compare(mVersion, version) < 0) {
                    list.add(new UpdatePackage(mDevice, filename, version, file.getString("size"),
                            file.getString("url"), file.getString("md5")));
                    if (Constants.DEBUG) Log.d(TAG, "A new version is found!");
                }
            }
        }
        Collections.sort(list, new Comparator<Updater.PackageInfo>() {

            @Override
            public int compare(Updater.PackageInfo lhs, Updater.PackageInfo rhs) {
                return Version.compare(lhs.getVersion(), rhs.getVersion());
            }

        });
        Collections.reverse(list);
        return list;
    }

    @Override
    public String getError() {
        return mError;
    }

}
