package backupclient.updater;

import backupclient.commons.*;

import java.net.MalformedURLException;
import java.net.URL;

public class AgentUpdater extends GenericUpdater {

    private final String agent_file_name = "Agent.jar";
    
    public AgentUpdater(Machine machine) {
        super(machine);
    }

    @Override
    protected Version current_version() {
        return machine.get_current_agent_version();
    }

    @Override
    protected Version selected_version() {
        return machine.selected_agent_version;
    }

    @Override
    protected String version_url() {
        return "clientversions/" + ((machine.auto_update) ?
                "current_agent/" : selected_version().name);
    }

    @Override
    protected String out_file_name() {
        return agent_file_name;
    }

    @Override
    protected URL download_link(Version v) {
        try {
            return new URL(v.agent_link);
        } catch (MalformedURLException e) {
            machine.log_error(e.toString());
            return null;
        }
    }

    @Override
    protected void download_done(Version new_version) {
        machine.set_current_agent_version(new_version);
    }

    public static void main(String... args) {

        System.out.println("Updater!!!");

        boolean lock = CrossProcessLock.instance.tryLock(0);
        if (!lock) return; // TODO log?

        String settingsLocation = "settings.xml";
        Machine machine = Machine.buildFromXmlSettings(new XMLHandler(settingsLocation));
        new AgentUpdater(machine).run();
    }
}