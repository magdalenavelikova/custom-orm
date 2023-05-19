package ormFramework.core;

import ormFramework.annotations.Entity;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class EntityScanner {
    private List<Class<?>> classes;

    public EntityScanner(Class<?> mainClass) throws URISyntaxException, ClassNotFoundException {
        String path = mainClass.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        String packageName = mainClass.getPackageName();
        File root = new File(path + packageName.replace(".", "/"));
        classes = new ArrayList<>();
        scanEntities(root, packageName);

    }

    private void scanEntities(File dir, String packageName) throws ClassNotFoundException {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                scanEntities(file, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {

                Class<?> classInfo = Class.forName(packageName + "." + file.getName().replace(".class", ""));
                if (classInfo.isAnnotationPresent(Entity.class)) {
                    this.classes.add(classInfo);
                }

            }
        }

    }

    public List<Class<?>> getClasses() {
        return classes;
    }
}
