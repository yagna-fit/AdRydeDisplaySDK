package com.adryde.mobile.displaysdk.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Decompress {
    private String zip;
    private String loc;

    public Decompress(String zipFile, String location) {
        //File folder = ctx.getDir("BPAProfiles", Context.MODE_PRIVATE);
        zip = zipFile;
        loc = location;
        dirChecker("");
    }

    public void unzip() {
        try  {
            FileInputStream fin = new FileInputStream(zip);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                Log.v("Decompress", "Unzipping " + ze.getName());

                if(ze.isDirectory()) {
                    dirChecker(ze.getName());
                } else {
                    String name = ze.getName();
                   if (name.indexOf(".") > 0)
                        name = name.substring(0, name.lastIndexOf("."));
                    FileOutputStream fout = new FileOutputStream(loc +"/"+name);
                    byte b[] = new byte[1024];
                    int n;
                    while ((n = zin.read(b,0,1024)) >= 0) {
                        fout.write(b,0,n);
                    }

                    zin.closeEntry();
                    fout.close();
                }

            }
            zin.close();
        } catch(Exception e) {
            Log.e("Decompress", "unzip", e);
        }

    }

    private void dirChecker(String dir) {
       File f = new File(loc + dir);

        if(!f.isDirectory()) {
            f.mkdirs();
        }
    }
}