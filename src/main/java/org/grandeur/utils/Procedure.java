package org.grandeur.utils;

public interface Procedure {
    void Run();

    default Procedure Then(Procedure after){
        return () -> {
            this.Run();
            after.Run();
        };
    }

    default Procedure Compose(Procedure before){
        return () -> {
            before.Run();
            this.Run();
        };
    }
}
