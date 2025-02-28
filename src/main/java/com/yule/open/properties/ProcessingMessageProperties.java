package com.yule.open.properties;

public enum ProcessingMessageProperties {
    FIND_ANNOTATION("Find all annotations...", "Only one annotation is found!"),
    FIND_JPA_DEPENDENCY("Find JPA dependency...", "JPA dependency is found!"),
    VALIDATE_REQUIRED_ENVIRONMENTS("Validate required environments...", "Required environments all provided!"),
    FIND_DB_URL("... Find database url...", "database url is found!"),
    FIND_USERNAME("... Find database username...", "database username is found!"),
    FIND_PASSWORD("... Find database password...", "database password is found!"),
    FIND_ENTITY_PATH("... Find entity path...", "entity path is found!"),
    ;

    private final String proc;
    private final String success;

    ProcessingMessageProperties(String proc, String suc) {
        this.proc = proc;
        this.success = suc;
    }

    public String getProc() {
        return proc;
    }

    public String getSuccess() {
        return success;
    }
}
