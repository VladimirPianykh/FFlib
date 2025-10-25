package com.bpa4j.util.codegen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Тест для демонстрации работы ProjectGraphV2 с JavaParser
 */
public class ProjectGraphV2Test {
    
    @Test
    public void testProjectGraphV2Parsing(@TempDir Path tempDir) throws IOException {
        // Создаем тестовую структуру проекта
        Path srcMainJava = tempDir.resolve("src/main/java");
        Files.createDirectories(srcMainJava);
        
        // Создаем тестовый файл Main.java с enum Permission и Role
        String mainJavaContent = """
            package com.test;
            
            public class Main {
                public enum AppPermission implements Permission {
                    READ_CUSTOMER,
                    CREATE_ORDER,
                    DELETE_PRODUCT
                }
                
                public enum AppRole implements Role {
                    ADMIN(
                        () -> Permission.values(),
                        () -> new Feature[]{}
                    ),
                    USER(
                        () -> new Permission[]{AppPermission.READ_CUSTOMER},
                        () -> new Feature[]{}
                    )
                }
            }
            """;
        
        Files.writeString(srcMainJava.resolve("Main.java"), mainJavaContent);
        
        // Создаем тестовый Editable класс
        String editableContent = """
            package com.test.editables;
            
            import com.bpa4j.core.Data.Editable;
            import com.bpa4j.editor.EditorEntry;
            
            public class Customer extends Editable {
                @EditorEntry(translation="Имя клиента")
                public String customerName;
                
                @EditorEntry(translation="Email")
                public String email;
                
                public Customer() {
                    super("Нов клиент");
                }
            }
            """;
        
        Files.createDirectories(srcMainJava.resolve("com/test/editables"));
        Files.writeString(srcMainJava.resolve("com/test/editables/Customer.java"), editableContent);
        
        // Создаем ProjectGraphV2 и тестируем парсинг
        ProjectGraphV2 projectGraph = new ProjectGraphV2(srcMainJava.toFile());
        
        // Проверяем, что узлы были созданы
        assert projectGraph.nodes.size() >= 3 : "Должно быть создано минимум 3 узла";
        
        // Выводим информацию о найденных узлах
        System.out.println("Найдено узлов: " + projectGraph.nodes.size());
        for (ProjectNode node : projectGraph.nodes) {
            System.out.println("Узел: " + node.getClass().getSimpleName() + " в файле " + node.location.getName());
        }
        
        // Тестируем работу с permissions
        PermissionsNodeV2 permissionsNode = projectGraph.nodes.stream()
            .filter(node -> node instanceof PermissionsNodeV2)
            .map(node -> (PermissionsNodeV2) node)
            .findFirst()
            .orElse(null);
        
        if (permissionsNode != null) {
            System.out.println("Найдены разрешения: " + permissionsNode.permissions);
            assert permissionsNode.permissions.contains("READ_CUSTOMER") : "Должно содержать READ_CUSTOMER";
            assert permissionsNode.permissions.contains("CREATE_ORDER") : "Должно содержать CREATE_ORDER";
            assert permissionsNode.permissions.contains("DELETE_PRODUCT") : "Должно содержать DELETE_PRODUCT";
        }
        
        // Тестируем работу с roles
        RolesNodeV2 rolesNode = projectGraph.nodes.stream()
            .filter(node -> node instanceof RolesNodeV2)
            .map(node -> (RolesNodeV2) node)
            .findFirst()
            .orElse(null);
        
        if (rolesNode != null) {
            System.out.println("Найдены роли: " + rolesNode.roles.size());
            for (RolesNodeV2.RoleRepresentation role : rolesNode.roles) {
                System.out.println("Роль: " + role.name + " с разрешениями: " + role.permissions);
            }
        }
        
        System.out.println("Тест ProjectGraphV2 завершен успешно!");
    }
}
