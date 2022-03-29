package ml.karmaconfigs.api.loader.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ml.karmaconfigs.api.common.karmafile.KarmaFile;
import ml.karmaconfigs.api.common.utils.file.FileUtilities;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import ml.karmaconfigs.api.loader.APILoader;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

public final class Loader {

    public Loader(final URL[] jars) {

    }

    public void load() {
        KarmaFile instructions = new KarmaFile(APILoader.source, "instructions.json");

        Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();
        JsonObject obj = new JsonObject();
        try {
            obj = gson.fromJson(
                    Files.newBufferedReader(instructions.getFile().toPath(), StandardCharsets.UTF_8),
                    JsonObject.class
            );
        } catch (Throwable ex) {
            List<String> lines = FileUtilities.readAllLines(instructions.getFile());
            if (StringUtils.listToString(lines, true).replaceAll("\\s", "").isEmpty()) {
                JsonArray array = new JsonArray();

                JsonObject exampleArgument = new JsonObject();
                exampleArgument.addProperty("argument", "--example-argument");
                exampleArgument.addProperty("valuable", true);
                exampleArgument.addProperty("value", "hello");

                JsonObject secondExample = new JsonObject();
                secondExample.addProperty("argument", "--test-argument");
                secondExample.addProperty("valuable", false);
                secondExample.addProperty("value", "");

                array.add(exampleArgument);
                array.add(secondExample);

                obj.add("JarName", array);
                try {
                    gson.toJson(obj,
                            Files.newBufferedWriter(instructions.getFile().toPath(), StandardCharsets.UTF_8, StandardOpenOption.CREATE));
                } catch (Throwable exc) {
                    exc.printStackTrace();
                }
            }
        }
    }
}
