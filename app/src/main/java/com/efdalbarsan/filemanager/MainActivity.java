package com.efdalbarsan.filemanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    int SDK_INT = 31;
    String dirPath;
    Spinner spnSelectFile;
    ListView lstFiles;
    ListView lstNewFiles;
    EditText edtEx;
    Button btnNewList;
    Button btnNewConvert;
    Spinner spnSelectEx;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> arrayListEx = new ArrayList<>();
    ArrayList<String> arrayLstFiles = new ArrayList<>();
    ArrayList<String> arrayNewLstFiles = new ArrayList<>();
    String root_sd = Environment.getExternalStorageDirectory().toString();
    String st;
    String selectExName;
    String exName;
    String exNameOld;


    private boolean checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }
    private void requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private static final int STORAGE_PERMISSION_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();
        checkPermission();

        lstFiles = findViewById(R.id.lstFiles);
        lstNewFiles = findViewById(R.id.lstFiles);
        spnSelectFile = findViewById(R.id.spnSelectFile);
        edtEx = findViewById(R.id.edtEx);
        btnNewList = findViewById(R.id.btnNewList);
        spnSelectEx = findViewById(R.id.spnSelectEx);
        btnNewConvert = findViewById(R.id.btnNewConvert);

        arrayListEx.add("png");
        arrayListEx.add("txt");
        arrayListEx.add("mp3");
        arrayListEx.add("mp4");
        arrayListEx.add("pdf");
        arrayListEx.add("jpg");
        arrayListEx.add("ALL");

        //Listeler
        ArrayAdapter arrayAdapterLstFiles= new ArrayAdapter(this, android.R.layout.simple_list_item_1,arrayLstFiles);
        ArrayAdapter arrayAdapterNewLstFiles= new ArrayAdapter(this, android.R.layout.simple_list_item_1,arrayNewLstFiles);

        getAllFolder();

        //ilk spinner
        ArrayAdapter<String> arrayAdapterEx = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,arrayListEx);
        arrayAdapterEx.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSelectEx.setAdapter(arrayAdapterEx);
        spnSelectEx.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectExName = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(adapterView.getContext(), "Selected: " + selectExName, Toast.LENGTH_LONG).show();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Klasor spinneri
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSelectFile.setAdapter(arrayAdapter);
        spnSelectFile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String tutorialsName = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Selected: " + tutorialsName, Toast.LENGTH_LONG).show();
                dirPath = root_sd + "/" + tutorialsName;
                arrayAdapterLstFiles.clear();
                getAllFile(dirPath);
                lstNewFiles.setAdapter(arrayAdapterNewLstFiles);

            }

            @Override
            public void onNothingSelected(AdapterView <?> parent)
            {
            }
        });
       //buton convert
        btnNewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lstFiles.setAdapter(arrayAdapterLstFiles);
            }
        });

        btnNewConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("New Convert ?");
                alert.setMessage("Do you want to make a new convert ?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //restart
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this,"Transaction successful", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
        });

    }

    //Spinnera klasoru listeler
    private void getAllFolder()
    {

       File file = new File(root_sd);
        getAllStuff(file, " ");
    }
    private void getAllStuff(File file, String name)
    {
        File list[] = file.listFiles();
        boolean folderFound = false;
        File mFile = null;
        String directoryName = "";

        for (int i = 0; i < list.length; i++)
        {

            mFile = new File(file, list[i].getName());
            Log.d("FileNameee", mFile.getName());
            Log.d("URIII", mFile.toURI().toString());
            if (mFile.isDirectory())
            {
                directoryName = list[i].getName();
                arrayList.add(list[i].getName());

            }else{
                arrayList.add(mFile.toURI().toString());

            }
        }
    }

    //Listeye Klasor icindeki Dosyalari Listeler
    public void  getAllFile(String path)
    {
        String newPath;
        Log.d("Files11", "Path: " + path);
        File directory = new File(path+"/");

        File[] files = directory.listFiles(File::isFile);
        st = edtEx.getText().toString();
        Log.d("Files22", "Size: "+ Objects.requireNonNull(files).length);
        for (File file : files) {
                int lengthOld = file.getName().length();
                int dotIndexOld = file.getName().lastIndexOf(".");
                exNameOld = file.getName().substring(dotIndexOld+1,lengthOld);
                if(selectExName == "ALL"){
                    System.out.println("All kismina girdi");
                    arrayNewLstFiles.add(file.getName());

                }else{
                    System.out.println("All kismina girmedi : " + selectExName);
                    if(exNameOld.equals(selectExName)){
                        arrayNewLstFiles.add(file.getName());
                    }
                }
                newPath = path + "/" + file.getName();
                int length = newPath.length();
                int dotIndex = newPath.lastIndexOf(".");
                exName = newPath.substring(dotIndex+1,length);
                if(selectExName == "ALL"){
                    System.out.println("All kismina girdi");
                    updateEx(newPath);
                }
                else if(exName.equals(selectExName))
                {
                    System.out.println("All kismina girmedi : " + selectExName);
                    updateEx(newPath);
                }else {
                System.out.println("yanlis: "+ exName);
                }
        }
    }

    //Uzanti degistirir.
    private void updateEx(String newPath){
        File oldfile = new File(newPath);;
        String substring = newPath.substring(0,newPath.lastIndexOf("."));
        String convertPath = substring + "." + st;
        File newfile = new File(convertPath);
        arrayLstFiles.add(newfile.getName());
        if(oldfile.renameTo(newfile)) {
            Log.d("Rename : ", "True");
        } else {
            Log.d("Rename : ", "False");

        }

    }

}