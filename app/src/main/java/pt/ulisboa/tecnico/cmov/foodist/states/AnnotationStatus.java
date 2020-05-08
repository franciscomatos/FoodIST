package pt.ulisboa.tecnico.cmov.foodist.states;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AnnotationStatus {
    public static final String PUBLIC = "PUBLIC";
    public static final String STUDENT = "STUDENT";
    public static final String PROFESSOR = "PROFESSOR";
    public static final String RESEARCHER = "RESEARCHER";
    public static final String STAFF = "STAFF";

    public AnnotationStatus(@Status String status){
    }

    @StringDef({PUBLIC, STUDENT, PROFESSOR, RESEARCHER, STAFF })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {}
}