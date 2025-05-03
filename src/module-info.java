module assignment {
    requires java.base;
    requires java.desktop;
    requires com.google.gson;
    requires com.github.librepdf.openpdf;
    requires org.bouncycastle.provider;
    requires org.bouncycastle.pkix;
    requires org.bouncycastle.util;
    requires activation;
    requires java.mail;
    
    exports assignmentapp;
    opens assignmentapp;
}