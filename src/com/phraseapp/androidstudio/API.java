package com.phraseapp.androidstudio;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class API {

    private final String accessToken;
    private final String workingDir;
    private final String clientPath;

    public API(String clientPath, String access_token, Project project) {
        this.accessToken = access_token;
        this.workingDir = project.getBasePath();
        this.clientPath = clientPath;
    }

    // Get all locales
    public APIResourceListModel getLocales(String projectId) {
        List<String> params = new ArrayList<String>();
        params.add(projectId);
        return runCommand("locales", "list", params);
    }

    // Get all projects
    public APIResourceListModel getProjects() {
        return runCommand("projects", "list", null);
    }

    // Create project
    public APIResourceListModel postProjects(String name) {
        List<String> params = new ArrayList<String>();
        params.add("--name");
        params.add(name);
        params.add("--main-format");
        params.add("xml");
        return runCommand("project", "create", params);
    }

    public APIResourceListModel postLocales(String projectId, String localeName) {
        String localeCode = localeName.replaceFirst("-r", "-");
        List<String> params = new ArrayList<String>();
        params.add(projectId);
        params.add("--name");
        params.add(localeName);
        params.add("--code");
        params.add(localeCode);
        return runCommand("locale", "create", params);
    }


    public APIResourceListModel uploadLocale(String projectId, String localeId, String file, String fileformat) {
        List<String> params = new ArrayList<String>();
        params.add(projectId);
        params.add("--locale-id");
        params.add(localeId);
        params.add("--file");
        params.add(file);
        params.add("--file-format");
        params.add(fileformat);
        return runCommand("upload", "create", params);
    }

    @Nullable
    private APIResourceListModel runCommand(String resource, String action, List<String> params) {
        APIResourceListModel resourceList = new APIResourceListModel();

        GeneralCommandLine gcl = new GeneralCommandLine(clientPath,
                resource);
        gcl.addParameter(action);


        if (params != null) {
            gcl.addParameters(params);
        }

        gcl.addParameter("--access-token");
        gcl.addParameter(accessToken);
        gcl.withWorkDirectory(workingDir);

        try {
            final CapturingProcessHandler processHandler = new CapturingProcessHandler(gcl.createProcess(), Charset.defaultCharset(), gcl.getCommandLineString());

            ProcessOutput output = processHandler.runProcess();
            String error = output.getStderr();

            if(!error.isEmpty()){

                resourceList.addError(error);
                return resourceList;
            }

            final String response = output.getStdout();
            APIResourceListModel resources = handleResponse(response);

            return resources;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return resourceList;
    }

    private APIResourceListModel handleResponse(String response) {
        if (!response.isEmpty()) {
            APIResourceListModel resourceList = new APIResourceListModel();

            if (response.startsWith("[")) {
                JSONArray objects = new JSONArray(response);

                for (int i = 0; i < objects.length(); i++) {
                    JSONObject pro = (JSONObject) objects.get(i);
                    resourceList.addElement(new APIResource((String) pro.get("id"), (String) pro.get("name")));
                }
            } else if (response.startsWith("{")) {
                JSONObject object = new JSONObject(response);
                if (object.has("name")) {
                    resourceList.addElement(new APIResource((String) object.get("id"), (String) object.get("name")));

                } else {
                    resourceList.addElement(new APIResource((String) object.get("id"), null));
                }
            } else {
                return null;
            }

            return resourceList;
        }
        return null;
    }

    public static boolean validateClient(String path, String workingDir) {
        GeneralCommandLine gcl = new GeneralCommandLine(path,
                "info");
        gcl.withWorkDirectory(workingDir);
        final CapturingProcessHandler processHandler;
        try {
            processHandler = new CapturingProcessHandler(gcl.createProcess(), Charset.defaultCharset(), gcl.getCommandLineString());
            ProcessOutput output = processHandler.runProcess();
            String response = output.getStdout();
            return response.toLowerCase().contains("phraseapp client version");
        } catch (ExecutionException e) {
        }

        return false;
    }


}