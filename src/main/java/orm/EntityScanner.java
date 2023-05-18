package orm;

import orm.annotations.Entity;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class EntityScanner {
    private List<Class<?>> classes;

    public EntityScanner(Class<?> mainClass) throws URISyntaxException, ClassNotFoundException {
        String path = mainClass.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        //String packageName = mainClass.getPackageName();
        File root = new File(path);
        classes = new ArrayList<>();
        scanEntities(root);
        System.out.println();
    }
    private void scanEntities(File dir) throws ClassNotFoundException {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                scanEntities(file);
            } else if (file.getName().endsWith(".class")) {
                System.out.println(file.getName());
                System.out.println(file.getPath());
                Class<?> classInfo = Class.forName(file.getName().replace(".class", "").trim());
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
