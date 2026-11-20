import com.change_vision.jude.api.inf.AstahAPI;
import com.change_vision.jude.api.inf.model.*;
import com.change_vision.jude.api.inf.presentation.IPresentation;
import com.change_vision.jude.api.inf.project.ProjectAccessor;

public class InspectLogicoAndComponentes {
    public static void main(String[] args) {
        try {
            ProjectAccessor accessor = AstahAPI.getAstahAPI().getProjectAccessor();
            accessor.open("RiskP_FINAL_MYSD.asta");
            IModel root = accessor.getProject();

            System.out.println("=== ALL DIAGRAMS ===");
            printDiagrams(root);

            accessor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printDiagrams(INamedElement element) {
        for (IDiagram d : element.getDiagrams()) {
            System.out.println("DIAGRAM: " + d.getName() + " in " + element.getName() + " (type: " + d.getClass().getSimpleName() + ")");
            try {
                for (IPresentation p : d.getPresentations()) {
                    String mName = (p.getModel() instanceof INamedElement) ? ((INamedElement) p.getModel()).getName() : "non-named";
                    System.out.println("   - " + p.getType() + " : " + p.getLabel() + " (model: " + mName + ")");
                }
            } catch (Exception e) {
                System.out.println("   - error getting presentations: " + e.getMessage());
            }
        }
        if (element instanceof IPackage) {
            for (INamedElement child : ((IPackage) element).getOwnedElements()) {
                printDiagrams(child);
            }
        }
    }
}
