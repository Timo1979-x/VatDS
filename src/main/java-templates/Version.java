package by.gto.jasperprintmysql;
public final class Version {

    private static final String VERSION = "${project.version}";
    private static final String GROUPID = "${project.groupId}";
    private static final String ARTIFACTID = "${project.artifactId}";
    private static final String REVISION = "${buildNumber}";
    private static final String DATEBUILD = "${dateBuild}";

    public static String getDATEBUILD() {
        return DATEBUILD;
    }

    public static String getVERSION() {
        return VERSION;
    }

    public static String getGROUPID() {
        return GROUPID;
    }

    public static String getARTIFACTID() {
        return ARTIFACTID;
    }

    public static String getREVISION() {
        return REVISION;
    }

}
