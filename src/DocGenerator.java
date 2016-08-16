package org.protocoder.documentation;

import com.sun.javadoc.*;
import com.google.gson.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DocGenerator {
  public static boolean start(RootDoc root) {

    Documentation documentation = new Documentation();
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    System.out.println("------------------>");
    ClassDoc[] classes = root.classes();
    for (int i = 0; i < classes.length; ++i) { // it class
      ProtoClass protoClass = new ProtoClass();
      protoClass.name = classes[i].name();
      documentation.put(protoClass.name, protoClass);

      System.out.println(classes[i]);

      // get fields with annotation protofield
      FieldDoc[] fields = classes[i].fields(); // it fields
      for (int j = 0; j < fields.length; j++) {

        boolean hasAnnotation = false;
        AnnotationDesc[] annotations = fields[j].annotations();
        for (AnnotationDesc annotation: annotations) {
          hasAnnotation |= annotation.annotationType().toString().equals("org.protocoderrunner.apidoc.annotation.ProtoMethod");
        }

        if (fields[j].isPublic() && hasAnnotation) {
          ProtoField protoField = new ProtoField();
          protoField.name = fields[j].name();
          protoClass.fields.put(protoField.name, protoField);
          System.out.println("\tf: " + fields[j].name());
        }
      }


      // get methods with annotation protomethod
      MethodDoc[] methods = classes[i].methods(); // it methods
      for (int j = 0; j < methods.length; j++) {

        AnnotationDesc[] annotations = methods[j].annotations();
        boolean hasAnnotation = false;
        for (AnnotationDesc annotation: annotations) {
          hasAnnotation |= annotation.annotationType().toString().equals("org.protocoderrunner.apidoc.annotation.ProtoMethod");
        }

        if (methods[j].isPublic() && hasAnnotation) {
          ProtoMethod protoMethod = new ProtoMethod();
          protoMethod.name = methods[j].name();
          protoClass.methods.put(protoMethod.name, protoMethod);

          System.out.println("\tm: " + methods[j].name());
          Parameter[] parameters = methods[j].parameters();
          for (int k = 0; k < parameters.length; k++) { // it parameters
            Parameter p = parameters[k];
            System.out.println("\t\t" + p.name() + ": " + p.type().qualifiedTypeName());
          }
          Tag[] tags = methods[j].tags("description");
          for (int k = 0; k < tags.length; k++) {
            protoMethod.description = tags[k].text();
            System.out.println("\t\t\t" + tags[k].text());
          }

        }
      } // it methods
    } // finish it class

    // TODO get path for each class

    System.out.println("<------------------");

    // generate JSON
    String json = gson.toJson(documentation);
    System.out.println(json);

    try (PrintStream out = new PrintStream(new FileOutputStream("documentation.json"))) {
        out.print(json);
    } catch (IOException e) {
      System.out.println("error saving json file");
    }

    return true;
  }

}


/*
 *  Classes that hold the documentation
 */
class Documentation extends HashMap {
}

class Proto {
  String name;
  String description;
  String locationFile;
  String example;
}

class ProtoClass extends Proto {
  HashMap<String, ProtoField> fields = new HashMap();
  HashMap<String, ProtoMethod> methods = new HashMap();
}

class ProtoField extends Proto {
}

class ProtoMethod extends Proto {
  ArrayList<ProtoSignatures> signatures = new ArrayList();
  HashMap<String, ProtoParams> params = new HashMap();
}

class ProtoParams extends Proto {
}

class ProtoSignatures {
  HashMap<Integer, String> params = new HashMap();
}
