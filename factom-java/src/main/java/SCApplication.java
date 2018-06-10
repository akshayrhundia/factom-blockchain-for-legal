

import controller.CaseController;
import controller.ChainController;
import controller.MSPController;
import utils.CORSResponseFilter;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class SCApplication extends Application {

    private final Set<Class<?>> resources = new HashSet<Class<?>>();
    private final Set<Object> singletons = new HashSet<Object>();

    public SCApplication() {

        // Add application resources
        this.resources.add(MSPController.class);
        this.resources.add(ChainController.class);
this.resources.add(CaseController.class);
        this.singletons.add(new CORSResponseFilter());

    }

    @Override
    public Set<Class<?>> getClasses() {
        return this.resources;
    }

    @Override
    public Set<Object> getSingletons() {
        return this.singletons;
    }
}
