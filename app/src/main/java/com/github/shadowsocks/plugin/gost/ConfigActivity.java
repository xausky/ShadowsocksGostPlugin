package com.github.shadowsocks.plugin.gost;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.shadowsocks.plugin.ConfigurationActivity;
import com.github.shadowsocks.plugin.PluginOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

public class ConfigActivity extends ConfigurationActivity {
    private LinearLayout linearlayout_cmdargs;
    private LinearLayout linearlayout_files;
    private Spinner argumentCountSpinner;
    private Editable newFileNameEditable;

    private Toast toast;

    private PluginOptions pluginOptions;
    private JSONObject decodedPluginOptions;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        try {
            savedInstanceState.putString("pluginOptions", this.pluginOptions.toString());
            this.saveUI();
            savedInstanceState.putString("decodedPluginOptions", this.decodedPluginOptions.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        savedInstanceState.putBoolean("onceAskedForConfigMigration", this.onceAskedForConfigMigration);
        savedInstanceState.putBoolean("onceAnsweredConfigMigrationPrompt", this.onceAnsweredConfigMigrationPrompt);
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        EditText editText_new_file_name = findViewById(R.id.editText_new_file_name);
        this.newFileNameEditable = editText_new_file_name.getText();
        this.onceAskedForConfigMigration = savedInstanceState.getBoolean("onceAskedForConfigMigration");
        this.onceAnsweredConfigMigrationPrompt = savedInstanceState.getBoolean("onceAnsweredConfigMigrationPrompt");
        String pluginOptions = savedInstanceState.getString("pluginOptions");
        if (pluginOptions != null) {
            this.pluginOptions = new PluginOptions(pluginOptions);
        }
        String json = savedInstanceState.getString("decodedPluginOptions");
        if (json != null) {
            try {
                this.decodedPluginOptions = new JSONObject(json);
                populateUI();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (this.onceAskedForConfigMigration && !this.onceAnsweredConfigMigrationPrompt) {
            // dialog will disappear after rotation, so pop it up again
            promptConfigMigration();
        }
    }

    private void showToast(int resID) {
        toast.cancel();
        toast.setText(resID);
        // toast.show(); // unexpectedly not shown. workaround below
        handler.post(new Runnable() {
            @Override
            public void run() {
                toast.show();
            }
        });
    }
    @Override
    protected void onInitializePluginOptions(@NonNull PluginOptions pluginOptions) {
        this.pluginOptions = pluginOptions;

        String encodedPluginOptions = pluginOptions.get("CFGBLOB");
        if (encodedPluginOptions == null || encodedPluginOptions.length() == 0) {
            // no CFGBLOB
            this.decodedPluginOptions = new JSONObject();

            // populate things to UI
            // and then they will be saved by saveUI() in onSaveInstanceState()

            // initial -L command argument
            String arg1 = getString(R.string.example_cmdarg1);
            String arg2 = getString(R.string.example_cmdarg2);
            addCmdArg(arg1, arg2, false, false);

            // initial 4 empty file entries
            for (int i = 0; i < fileNameList.length; i++) {
                String fileName = fileNameList[i];
                String fileData = "";
                String fileHint = getString(fileHintList[i]);
                addFileEntry(fileName, fileData, fileHint, false);
            }

            if (pluginOptions.toString().length() == 0) {
                // nothing here, just empty
                showToast(R.string.empty_config);
            } else {
                // found old style cmdline options, prompt to migrate to CFGBLOB
                promptConfigMigration();
            }
        } else {
            // has CFGBLOB, ignoring other keys
            Base64 base64 = new Base64();
            try {
                base64.setPaddingChar('_');
                String json = new String(base64.decode(encodedPluginOptions), StandardCharsets.UTF_8);
                this.decodedPluginOptions = new JSONObject(json);

                populateUI();

                showToast(R.string.loaded_cfgblob);
            } catch (Exception e) {
                e.printStackTrace();
                this.decodedPluginOptions = new JSONObject();
                showToast(R.string.err_loading_cfgblob);
                fallbackToManualEditor();
            }
        }
    }
    private AlertDialog configMigrationDialog;
    private boolean onceAskedForConfigMigration = false;
    private boolean onceAnsweredConfigMigrationPrompt = false;
    private void promptConfigMigration() {
        onceAskedForConfigMigration = true;
        if (configMigrationDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.prompt_config_mig_title);
            builder.setMessage(R.string.prompt_config_mig_msg);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onceAnsweredConfigMigrationPrompt = true;
                    // do migration
                    try {
                        // populate things to UI
                        // and then they will be saved by saveUI() in onSaveInstanceState()

                        // populate original plugin options string to UI
                        final String legacyCfg = pluginOptions.toString();
                        populateLegacyCfg(legacyCfg);

                        // populate command line arguments to UI
                        ArrayList<String> substrings = new ArrayList<>();
                        for (String s : legacyCfg.split(" ")) {
                            if (s.length() == 0)
                                continue;
                            substrings.add(s);
                        }
                        for (int i = 0; i < substrings.size(); i++) {
                            // "-L" should already be added
                            boolean allowDelete = cmdArgIdx.size() > 0;
                            String s = substrings.get(i);
                            String next = null;
                            if (i + 1 < substrings.size())
                                next = substrings.get(i + 1);
                            if (
                                    s.matches("^-[A-Za-z0-1]$")
                                    && next != null
                                    && !next.matches("^-[A-Za-z0-1]$")
                               )
                            {
                                addCmdArg(s, next, allowDelete, false);
                                i++;
                            } else {
                                addCmdArg("", s, allowDelete, true);
                            }
                        }

                        toast.setText(R.string.config_mig_done);
                    } catch (Exception e) {
                        e.printStackTrace();
                        toast.setText(R.string.config_mig_err);
                        fallbackToManualEditor();
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onceAnsweredConfigMigrationPrompt = true;
                    toast.setText(R.string.cancelled);
                    fallbackToManualEditor();
                }
            });
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (!onceAnsweredConfigMigrationPrompt) {
                        toast.cancel();
                        configMigrationDialog.show(); // didn't click cancel button, so ask again
                    } else {
                        toast.show();
                    }
                }
            });
            configMigrationDialog = builder.create();
        }
        toast.cancel();
        configMigrationDialog.show();
    }


    private HashMap<Long, Editable[]> cmdArgMap;
    private ArrayList<Long> cmdArgIdx;
    private long cmdArgCtr = 0;

    private HashMap<String, Editable> fileDataMap;

    private void regenerateIDs(View v) {
        // regenerate new resIDs recursively for every children
        // otherwise later newly generated objects are "tied" to older one
        // like, after rotation change
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                regenerateIDs(vg.getChildAt(i));
            }
        }
        v.setId(View.generateViewId());
    }

    private void confirmDelCmdArg(long index, final View child) {
        final long currentIndex = index;
        final LinearLayout parent = this.linearlayout_cmdargs;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm_del_arg_title);
        StringBuilder msg = new StringBuilder(getString(R.string.confirm_del_arg_msg) + "\n");
        Editable[] array = cmdArgMap.get(currentIndex);
        if (array != null) {
            for (Editable e : array) {
                msg.append("\"").append(e.toString()).append("\" ");
            }
            msg.deleteCharAt(msg.length() - 1);
        } else Log.d("ConfigActivity", "confirmDelCmdArg cmdArgMap.get(currentIndex) == null");
        builder.setMessage(msg.toString());
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cmdArgMap.remove(currentIndex);
                cmdArgIdx.remove(currentIndex);
                parent.removeView(child);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }
    private void addCmdArg(String arg1, String arg2, boolean allowDelete, boolean hideFirstArg) {
        final ViewGroup parent = this.linearlayout_cmdargs;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") final View child = inflater.inflate(R.layout.cmdarg, null);

        EditText cmdarg1 = child.findViewById(R.id.editText_cmdarg1);
        EditText cmdarg2 = child.findViewById(R.id.editText_cmdarg2);
        Button button_del = child.findViewById(R.id.button_del);

        regenerateIDs(child);

        button_del.setEnabled(allowDelete);
        if (!allowDelete) {
            button_del.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C0C0C0")));
        }
        if (hideFirstArg) {
            cmdarg1.setVisibility(View.GONE);
            cmdarg2.setHint("");
        } else {
            cmdarg1.setText(arg1);
        }
        cmdarg2.setText(arg2);

        Editable[] twoArgs = {cmdarg1.getText(), cmdarg2.getText()};
        Editable[] oneArg = {twoArgs[1]};
        final Editable[] array = hideFirstArg ? oneArg : twoArgs;
        final long currentIndex = ++cmdArgCtr;
        cmdArgMap.put(currentIndex, array);
        cmdArgIdx.add(currentIndex);

        button_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelCmdArg(currentIndex, child);
            }
        });

        parent.addView(child);
    }

    private void confirmDelFile(final String fileName, final View child) {
        final LinearLayout parent = this.linearlayout_files;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm_del_file_title);
        builder.setMessage(getString(R.string.confirm_del_file_msg) + fileName);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fileDataMap.remove(fileName);
                parent.removeView(child);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }
    private void addFileEntry(final String fileName, final String fileData, String hint, boolean isDeletable) {
        final ViewGroup parent = this.linearlayout_files;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") final View child = inflater.inflate(R.layout.fileentry, null);

        TextView fileNameLabel = child.findViewById(R.id.text_file_name);
        Button button_del_file = child.findViewById(R.id.button_del_file);
        EditText fileDataEditText = child.findViewById(R.id.editText_file_data);

        regenerateIDs(child);

        fileNameLabel.setText(fileName);
        if (!isDeletable) {
            button_del_file.setEnabled(false);
            button_del_file.setVisibility(View.GONE);
        }
        fileDataEditText.setHint(hint);
        fileDataEditText.setText(fileData);

        fileDataMap.put(fileName, fileDataEditText.getText());

        button_del_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDelFile(fileName, child);
            }
        });

        parent.addView(child);
    }

    private final String[] fileNameList = {
            "config.json",
            "cacert.pem",
            "clientcert.pem",
            "clientcertkey.pem",
    };
    private final int[] fileHintList = {
            R.string.example_cfgjson,
            R.string.example_cacert,
            R.string.example_clientcert,
            R.string.example_clientcertkey,
    };

    private void saveUI() throws NullPointerException, JSONException {
        if (this.decodedPluginOptions == null)
            this.decodedPluginOptions = new JSONObject();

        // save linearlayout_cmdargs
        JSONArray allArgs = new JSONArray();
        for (Long index : cmdArgIdx) {
            Editable[] oneOrTwoArgsEditable = cmdArgMap.get(index);
            if (oneOrTwoArgsEditable == null) {
                Log.e("ConfigActivity", "saveUI encountered oneOrTwoArgs == null");
                throw new NullPointerException();
            }
            JSONArray oneOrTwoArgs = new JSONArray();
            for (Editable oneOfArgs : oneOrTwoArgsEditable) {
                String arg = oneOfArgs.toString();
                if (arg.startsWith("\"") && arg.endsWith("\""))
                    arg = arg.substring(1, arg.length() - 1);
                // oneOrTwoArgs.put("\"" + arg + "\""); // adding quotes leads to crash
                oneOrTwoArgs.put(arg);
            }
            allArgs.put(oneOrTwoArgs);
        }
        // Note: Golang requires exported identifiers to begin with upper-case alphabet
        // The same to below
        this.decodedPluginOptions.put("CmdArgs", allArgs);

        // save files
        JSONObject files = new JSONObject();
        for (Map.Entry<String, Editable> entry : fileDataMap.entrySet()) {
            files.put(entry.getKey(), entry.getValue().toString());
        }
        this.decodedPluginOptions.put("Files", files);

        // save legacyCfg, if there's one
        String legacyCfg = "";
        EditText editText_legacyCfg = findViewById(R.id.editText_legacyCfg);
        Editable editable_legacyCfg = editText_legacyCfg.getText();
        if (editable_legacyCfg != null) {
            legacyCfg = editable_legacyCfg.toString();
        }
        if (legacyCfg.length() > 0) {
            this.decodedPluginOptions.put("LegacyCfg", legacyCfg);
        } else {
            this.decodedPluginOptions.remove("LegacyCfg");
        }

        // (not UI, but also saved here) save app data directory path
        File dataDir = new ContextWrapper(getApplicationContext()).getFilesDir();
        if (!dataDir.exists() && !dataDir.mkdirs()) {
            Log.e("ConfigActivity", "dataDir.mkdirs() failed");
        }
        this.decodedPluginOptions.put("DataDir", dataDir.getAbsolutePath());
    }
    private void populateUI() throws JSONException {
        // populate linearlayout_cmdargs
        JSONArray array = this.decodedPluginOptions.getJSONArray("CmdArgs");
        for (int i = 0; i < array.length(); i++) {
            JSONArray oneOrTwoArgs = array.getJSONArray(i);
            // remove quotes at the beginning and the end
            String[] arg = new String[oneOrTwoArgs.length()];
            for (int j = 0; j < oneOrTwoArgs.length(); j++) {
                String s = oneOrTwoArgs.getString(j);
                if (s.matches("^\".*\"$"))
                    s = s.substring(1, s.length() - 1);
                arg[j] = s;
            }
            // the first argument should be "-L", generally considered necessary
            boolean allowDelete = this.cmdArgIdx.size() > 0;
            // sometimes it's more convenient to use only one edit box instead of two
            if (oneOrTwoArgs.length() == 1) {
                this.addCmdArg("", arg[0], allowDelete, true);
            } else {
                this.addCmdArg(arg[0], arg[1], allowDelete, false);
            }
        }

        // populate linearlayout_files
        // read from decodedPluginOptions, if fails, use empty jsonObject
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = this.decodedPluginOptions.getJSONObject("Files");
        } catch (JSONException ignored) {}
        // ensure that every file name in fileNameList exist in jsonObject
        for (String fileName : fileNameList) {
            if (jsonObject.has(fileName)) {
                continue;
            } else try {
                jsonObject.getString(fileName);
                continue;
            } catch (JSONException ignored) {}
            jsonObject.put(fileName, "");
        }
        // add files in fileNameList first
        Set<String> fixedFiles = new HashSet<>();
        for (int i = 0; i < fileNameList.length; i++) {
            String fileName = fileNameList[i];
            String fileData = jsonObject.getString(fileName);
            String fileHint = getString(fileHintList[i]);
            fixedFiles.add(fileName);
            addFileEntry(fileName, fileData, fileHint, false);
        }
        // add remaining files, if any
        for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
            String fileName = it.next();
            if (fixedFiles.contains(fileName))
                continue;
            addFileEntry(fileName, jsonObject.getString(fileName), "", true);
        }

        // populate legacyCfg, if there's one
        String legacyCfg = "";
        try {
            legacyCfg = this.decodedPluginOptions.getString("LegacyCfg");
        } catch (JSONException ignored) {}
        this.populateLegacyCfg(legacyCfg);
    }
    private void populateLegacyCfg(String legacyCfg) {
        boolean hasLegacyCfg = legacyCfg != null && legacyCfg.length() > 0;
        Button button_revert_to_legacy_config = findViewById(R.id.button_revert_to_legacy_config);
        button_revert_to_legacy_config.setClickable(hasLegacyCfg);
        button_revert_to_legacy_config.setEnabled(hasLegacyCfg);
        EditText editText_legacyCfg = findViewById(R.id.editText_legacyCfg);
        editText_legacyCfg.setEnabled(hasLegacyCfg);
        editText_legacyCfg.setText(hasLegacyCfg ? legacyCfg : "");
        LinearLayout linearlayout_legacyCfg = findViewById(R.id.linearlayout_legacyCfg);
        linearlayout_legacyCfg.setVisibility(hasLegacyCfg ? View.VISIBLE : View.GONE);
    }

    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_activity);

        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        handler = new Handler(); // workaround toast unexpectedly not showing problem

        cmdArgMap = new HashMap<>();
        cmdArgIdx = new ArrayList<>();

        fileDataMap = new HashMap<>();

        argumentCountSpinner = findViewById(R.id.spinner_add_one_or_two_args);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.string_add_one_or_two_args, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        argumentCountSpinner.setAdapter(adapter);
        argumentCountSpinner.setSelection(1, false);

        linearlayout_cmdargs = findViewById(R.id.linearlayout_cmdargs);
        Button button_add = findViewById(R.id.button_add);

        linearlayout_files = findViewById(R.id.linearlayout_files);
        EditText editText_new_file_name = findViewById(R.id.editText_new_file_name);
        Button button_add_file = findViewById(R.id.button_add_file);

        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hideFirstArg = argumentCountSpinner.getSelectedItemPosition() == 0;
                String arg2 = hideFirstArg ? "" : getString(R.string.example_cmdarg4);
                addCmdArg(getString(R.string.example_cmdarg3), arg2, true, hideFirstArg);
            }
        });

        this.newFileNameEditable = editText_new_file_name.getText();
        button_add_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = newFileNameEditable.toString();
                if (fileName.length() == 0) {
                    showToast(R.string.err_file_name_empty);
                    return;
                }
                if (fileName.contains("/")) {
                    showToast(R.string.err_file_name_contains_slash);
                    return;
                }
                if (fileDataMap.containsKey(fileName)) {
                    showToast(R.string.err_file_already_exists);
                    return;
                }
                addFileEntry(fileName, "", "", true);
            }
        });

        Button button_revert_to_legacy_config = findViewById(R.id.button_revert_to_legacy_config);
        button_revert_to_legacy_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = getString(R.string.confirm_revert_to_legacy_config_title);
                String msg = getString(R.string.confirm_revert_to_legacy_config_msg);
                String positiveButton = getString(R.string.ok);
                String negativeButton = getString(R.string.cancel);
                RunnableEx positive = new RunnableEx() {
                    @Override
                    public void run() {
                        EditText editText_legacyCfg = findViewById(R.id.editText_legacyCfg);
                        String legacyCfg = editText_legacyCfg.getText().toString();
                        saveChanges(new PluginOptions(legacyCfg));
                        finish();
                    }
                };
                Runnable negative = new Runnable() {
                    @Override
                    public void run() {
                    }
                };
                String toastMsgOnSuccess = getString(R.string.reverted_to_legacy_config);
                String toastMsgOnFail = getString(R.string.error_reverting_to_legacy_config);
                String toastMsgOnCancel = getString(R.string.cancelled);
                askForConsent(title, msg, positiveButton, negativeButton, positive, negative, toastMsgOnSuccess, toastMsgOnFail, toastMsgOnCancel);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // ask for save & apply
        String title = getString(R.string.confirm_save_apply_title);
        String msg = getString(R.string.confirm_save_apply_msg);
        String positiveButton = getString(R.string.ok);
        String negativeButton = getString(R.string.discard_changes);
        RunnableEx positive = new RunnableEx() {
            @Override
            public void run() throws JSONException, Base64.Base64Exception {
                saveUI();

                String json = decodedPluginOptions.toString();
                Base64 base64 = new Base64();
                base64.setPaddingChar('_');
                String encodedPluginOptions = base64.encode(json.getBytes(StandardCharsets.UTF_8));
                pluginOptions.clear(); // discard keys other than CFGBLOB
                pluginOptions.put("CFGBLOB", encodedPluginOptions);
                saveChanges(pluginOptions);
                finish();
            }
        };
        Runnable negative = new Runnable() {
            @Override
            public void run() {
                finish();
            }
        };
        String toastMsgOnSuccess = getString(R.string.saved_cfgblob);
        String toastMsgOnFail = getString(R.string.error_saving_cfgblob);
        String toastMsgOnCancel = getString(R.string.cancelled);
        askForConsent(title, msg, positiveButton, negativeButton, positive, negative, toastMsgOnSuccess, toastMsgOnFail, toastMsgOnCancel);
    }

    private boolean dismissedConsent = false;
    private void askForConsent(
            String title, String msg,
            String positiveButton, String negativeButton,
            final RunnableEx positive, final Runnable negative,
            final String toastMsgOnSuccess, final String toastMsgOnFail, final String toastMsgOnCancel)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissedConsent = false;
                try {
                    positive.run();
                } catch (Exception e) {
                    e.printStackTrace();
                    toast.setText(toastMsgOnFail);
                    return;
                }
                toast.setText(toastMsgOnSuccess);
            }
        });
        builder.setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissedConsent = false;
                negative.run();
                toast.setText(toastMsgOnCancel);
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (dismissedConsent)
                    toast.setText(toastMsgOnCancel);
                toast.show();
            }
        });
        AlertDialog consentDialog = builder.create();

        toast.cancel();
        dismissedConsent = true;
        consentDialog.show();
    }

}