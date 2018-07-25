package sg.acecom.track.systempest.model;

/**
 * Created by jmingl on 26/6/18.
 */

public class Findings {

    String PestDesc;
    String AreaConcerned;
    String Findings;

    public String getFindings() {
        return Findings;
    }

    public void setFindings(String findings) {
        Findings = findings;
    }

    public Findings(){

    }

    public Findings(String findings) {
        Findings = findings;
    }

    public Findings(String pestDesc, String areaConcerned, String findings) {
        PestDesc = pestDesc;
        AreaConcerned = areaConcerned;
        Findings = findings;
    }

    public String getPestDesc() {
        return PestDesc;
    }

    public void setPestDesc(String pestDesc) {
        PestDesc = pestDesc;
    }

    public String getAreaConcerned() {
        return AreaConcerned;
    }

    public void setAreaConcerned(String areaConcerned) {
        AreaConcerned = areaConcerned;
    }
}
