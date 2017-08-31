package com.example.annotation.database;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TableCreator {
    public static void main(String[] args) throws ClassNotFoundException {
        if (args.length < 1) {
            System.out.println("Specify Java Bean");
            return;
        }

        // assume args only contain one JavaBean
        String className = args[0];
        Class<?> cl = Class.forName(className);

        String tableName = null;
        DBTable dbTable = cl.getAnnotation(DBTable.class);
        if (dbTable == null) {
            System.out.println("No DBTable Annotation in class" + className);
            return;
        }
        if (dbTable.name().isEmpty()) {
            tableName = cl.getName().toUpperCase();
        } else {
            tableName = dbTable.name();
        }

        List<String> columnDefs = new ArrayList<>();
        for (Field field : cl.getDeclaredFields()) {
            Annotation[] annotations = field.getDeclaredAnnotations();
            if (annotations.length < 1) {
                // field have no annotation
                continue;
            }
            Annotation dbAnnotation = annotations[0];
            if (dbAnnotation instanceof SQLInteger) {
                SQLInteger sInt = ((SQLInteger) dbAnnotation);
                String columnName = null;
                if (sInt.name().isEmpty()) {
                    columnName = field.getName().toUpperCase();
                } else {
                    columnName = sInt.name();
                }
                columnDefs.add(columnName + " INT" + getConstraints(sInt.constraints()));
            } else if (dbAnnotation instanceof SQLString) {
                SQLString sStr = ((SQLString) dbAnnotation);
                String columnName = null;
                if (sStr.name().isEmpty()) {
                    columnName = field.getName().toUpperCase();
                } else {
                    columnName = sStr.name();
                }
                columnDefs.add(columnName + " VARCHAR(" + sStr.value() + ")" + getConstraints(sStr.constraints()));
            }
        }

        // construct SQL commend
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE " + tableName + "(");
        for (String columnDef : columnDefs) {
            sb.append("\n    " + columnDef + ",");
        }
        String sqlCommend = sb.substring(0, sb.length() - 1) + ");";
        System.out.println(sqlCommend);
    }

    private static String getConstraints(Constraints con) {
        StringBuilder sb = new StringBuilder();
        if (!con.allowNull()) {
            sb.append(" NOT NULL");
        }
        if (con.primaryKey()) {
            sb.append(" PRIMARY KEY");
        }
        if (con.unique()) {
            sb.append(" UNIQUE");
        }
        return sb.toString();
    }
}
