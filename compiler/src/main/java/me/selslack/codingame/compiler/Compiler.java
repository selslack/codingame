package me.selslack.codingame.compiler;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Compiler {
    private final String _fqcn;
    private final List<File> _paths;

    private final Map<String, TypeDeclaration> _classes = new HashMap<>();
    private final Map<String, List<ImportDeclaration>> _deps = new HashMap<>();

    private final Map<String, TypeDeclaration> _compiledClasses = new HashMap<>();
    private final Set<ImportDeclaration> _compiledImports = new HashSet<>();

    Compiler(String fqcn, List<File> paths) {
        _fqcn = fqcn;
        _paths = paths;
    }

    Compiler(String fqcn, File[] paths) {
        this(fqcn, Arrays.asList(paths));
    }

    public String compile() throws IOException {
        CompilationUnit result = new CompilationUnit();

        for (File path : _paths) {
            for (File file : FileUtils.listFiles(path, new String[]{"java"}, true)) {
                try {
                    new DependenciesCollectorVisitor().visit(JavaParser.parse(file), null);
                }
                catch (ParseException ignored) {
                    // ignored
                }
            }
        }

        try {
            String mainClass;

            if (_classes.containsKey(_fqcn)) {
                mainClass = _fqcn;
            }
            else {
                mainClass = _classes
                    .entrySet()
                    .stream()
                    .filter(v -> v.getKey().endsWith(_fqcn))
                    .findFirst()
                    .orElseThrow(() -> new ClassNotFoundException(_fqcn))
                    .getKey();
            }

            _addLocalPackageDependency(mainClass);
            _addClass(mainClass);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        _compiledClasses.forEach((k, v) -> v.setModifiers(v.getModifiers() & ~ModifierSet.PUBLIC));

        result.setTypes(new ArrayList<>(_compiledClasses.values()));
        result.setImports(new ArrayList<>(_compiledImports));

        return result.toStringWithoutComments();
    }

    private void _addLocalPackageDependency(String clazz) {
        String pkg = Pattern.compile("\\.[^\\.]*$").matcher(clazz).replaceAll("");
        ImportDeclaration imp = new ImportDeclaration(new NameExpr(pkg), false, true);

        if (_deps.containsKey(clazz)) {
            _deps.get(clazz).add(imp);
        }
        else {
            _deps.put(clazz, Collections.singletonList(imp));
        }
    }

    private boolean _addClass(String clazz) throws ClassNotFoundException {
        if (!_classes.containsKey(clazz)) {
            throw new ClassNotFoundException(clazz);
        }

        TypeDeclaration source = _classes.get(clazz);

        if (_compiledClasses.containsKey(source.getName())) {
            if (_compiledClasses.get(source.getName()) == source) {
                return true;
            }
            else {
                throw new RuntimeException("Duplicate class name: " + clazz);
            }
        }

        _compiledClasses.put(source.getName(), source);

        for (ImportDeclaration imp : _deps.get(clazz)) {
            if (imp.isStatic()) {
                throw new RuntimeException("Can not compile static imports: " + imp);
            }

            if (imp.isAsterisk()) {
                Pattern check = Pattern.compile(
                    "^" + Pattern.quote(imp.getName().toString() + ".") + "[^\\.]+"
                );

                boolean found = false;

                for (Map.Entry<String, TypeDeclaration> clz : _classes.entrySet()) {
                    if (check.matcher(clz.getKey()).matches()) {
                        found = _addClass(clz.getKey());
                    }
                }

                if (!found) {
                    _addImport(imp);
                }
            }
            else {
                try {
                    _addClass(imp.getName().toString());
                }
                catch (ClassNotFoundException e) {
                    _addImport(imp);
                }
            }
        }

        return true;
    }

    private void _addImport(ImportDeclaration imp) {
        if (_compiledImports.contains(imp)) {
            return ;
        }

        _compiledImports.add(imp);
    }

    private class DependenciesCollectorVisitor extends VoidVisitorAdapter {
        String _package;
        List<ImportDeclaration> _imports;

        @Override
        public void visit(PackageDeclaration n, Object arg) {
            _package = n.getName().toStringWithoutComments();
            _imports = new LinkedList<>();
        }

        @Override
        public void visit(ImportDeclaration n, Object arg) {
            if (_package == null) {
                return;
            }

            _imports.add(n);
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Object arg) {
            if (_package == null) {
                return;
            }

            if (ModifierSet.isPublic(n.getModifiers())) {
                _classes.putIfAbsent(_package + "." + n.getName(), n);
                _deps.putIfAbsent(_package + "." + n.getName(), _imports);
            }
        }

        @Override
        public void visit(EnumDeclaration n, Object arg) {
            if (_package == null) {
                return;
            }

            if (ModifierSet.isPublic(n.getModifiers()) || ModifierSet.isProtected(n.getModifiers()) || ModifierSet.hasPackageLevelAccess(n.getModifiers())) {
                _classes.putIfAbsent(_package + "." + n.getName(), n);
                _deps.putIfAbsent(_package + "." + n.getName(), _imports);
            }
        }
    }
}
