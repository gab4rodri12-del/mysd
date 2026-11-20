import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.editor.BasicModelEditor;
import com.change_vision.jude.api.inf.editor.ModelEditorFactory;
import com.change_vision.jude.api.inf.editor.TransactionManager;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class FixHierarchy {
    public static void main(String[] args) {
        String path = "RiskP_FINAL_MYSD.asta";
        try {
            ProjectAccessor accessor = AstahAPI.getAstahAPI().getProjectAccessor();
            accessor.open(path);
            IModel root = accessor.getProject();

            TransactionManager.beginTransaction();
            BasicModelEditor editor = ModelEditorFactory.getBasicModelEditor();

            // Find "1. Ciclo Uno" recursively
            IPackage cicloUno = findPackageRecursive(root, "1. Ciclo Uno");
            System.out.println("cicloUno found: " + (cicloUno != null ? cicloUno.getFullName(".") : "null"));

            // Find "Datos" and "Componentes"
            IPackage pkgDatos = findPackageRecursive(root, "Datos");
            IPackage pkgComponentes = null;
            for (INamedElement e : root.getOwnedElements()) {
                if (e instanceof IPackage && e.getName().equals("Componentes")) {
                    pkgComponentes = (IPackage) e;
                    break;
                }
            }
            if (pkgComponentes == null) {
                pkgComponentes = findPackageRecursive(root, "Componentes");
            }

            // Find or create "3. Fisico" inside cicloUno
            IPackage fisico1 = findPackageChild(cicloUno, "3. Fisico");
            if (fisico1 == null) {
                System.out.println("Creating 3. Fisico inside cicloUno...");
                fisico1 = editor.createPackage(cicloUno, "3. Fisico");
            }

            if (pkgDatos != null) {
                System.out.println("Moving pkgDatos to fisico1...");
                editor.changeParent(pkgDatos, fisico1);
            }

            if (pkgComponentes != null) {
                System.out.println("Moving pkgComponentes to fisico1...");
                editor.changeParent(pkgComponentes, fisico1);
            }

            IPackage actores1 = findPackageChild(fisico1, "Actores");
            if (actores1 == null) {
                System.out.println("Creating Actores inside fisico1...");
                editor.createPackage(fisico1, "Actores");
            }

            // Also check 2. Ciclo Dos
            IPackage cicloDos = findPackageRecursive(root, "2. Ciclo Dos");
            if (cicloDos != null && cicloDos.getOwner() != cicloUno.getOwner()) {
                System.out.println("Moving cicloDos to match cicloUno owner...");
                editor.changeParent(cicloDos, (INamedElement) cicloUno.getOwner());
            }

            TransactionManager.endTransaction();
            accessor.save();
            accessor.close();
            System.out.println("FIX HIERARCHY COMPLETE!");

        } catch (Exception e) {
            TransactionManager.abortTransaction();
            e.printStackTrace();
        }
    }

    private static IPackage findPackageChild(IPackage parent, String name) {
        if (parent == null) return null;
        for (INamedElement elem : parent.getOwnedElements()) {
            if (elem instanceof IPackage && elem.getName().equalsIgnoreCase(name)) {
                return (IPackage) elem;
            }
        }
        return null;
    }

    private static IPackage findPackageRecursive(IPackage parent, String name) {
        if (parent == null) return null;
        for (INamedElement elem : parent.getOwnedElements()) {
            if (elem instanceof IPackage) {
                if (elem.getName().equalsIgnoreCase(name)) {
                    return (IPackage) elem;
                }
                IPackage found = findPackageRecursive((IPackage) elem, name);
                if (found != null) return found;
            }
        }
        return null;
    }
}
















































































































