package de.trispeedys.resourceplanning.test;

import de.trispeedys.resourceplanning.entity.Domain;
import de.trispeedys.resourceplanning.entity.Event;
import de.trispeedys.resourceplanning.entity.EventTemplate;
import de.trispeedys.resourceplanning.entity.Helper;
import de.trispeedys.resourceplanning.entity.Position;
import de.trispeedys.resourceplanning.entity.misc.EventState;
import de.trispeedys.resourceplanning.entity.misc.HelperState;
import de.trispeedys.resourceplanning.entity.util.EntityFactory;
import de.trispeedys.resourceplanning.repository.HelperRepository;
import de.trispeedys.resourceplanning.repository.base.RepositoryProvider;
import de.trispeedys.resourceplanning.util.SpeedyRoutines;

public class TestDataGenerator
{
    public static final int POS_COUNT_SIMPLE_EVENT = 5;

    private static final String DEFAULT_MAIL_ADDRESS = "testhelper1.trispeedys@gmail.com";

    private static final Integer PRIO1 = new Integer(1);

    private static final Integer PRIO2 = new Integer(2);

    /**
     * creates an {@link Event} like {@link TestDataGenerator#createMinimalEvent(String, String, int, int, int)}, but
     * without assignments of positions to helpers.
     * 
     * @return
     */
    public static Event createSimpleUnassignedEvent(String description, String eventKey, int day, int month, int year)
    {
        EventTemplate template = EntityFactory.buildEventTemplate("123").saveOrUpdate();

        // build event
        Event myLittleEvent = EntityFactory.buildEvent(description, eventKey, day, month, year, EventState.PLANNED, template, null).saveOrUpdate();

        // create helpers
        EntityFactory.buildHelper("H1_First", "H1_Last", DEFAULT_MAIL_ADDRESS, HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        EntityFactory.buildHelper("H3_First", "H3_Last", DEFAULT_MAIL_ADDRESS, HelperState.ACTIVE, 3, 1, 1980).saveOrUpdate();

        // build domains
        Domain domain1 = EntityFactory.buildDomain("D1", 1).saveOrUpdate();
        Domain domain2 = EntityFactory.buildDomain("D2", 2).saveOrUpdate();
        // build positions
        Position pos1 = EntityFactory.buildPosition("P1", 12, domain1, 0, true).saveOrUpdate();
        Position pos2 = EntityFactory.buildPosition("P2", 12, domain1, 1, true).saveOrUpdate();
        Position pos3 = EntityFactory.buildPosition("P3", 12, domain2, 2, true).saveOrUpdate();
        Position pos4 = EntityFactory.buildPosition("P4", 12, domain2, 3, true).saveOrUpdate();
        Position pos5 = EntityFactory.buildPosition("P5", 12, domain2, 4, true).saveOrUpdate();
        // assign positions to event
        SpeedyRoutines.relatePositionsToEvent(myLittleEvent, pos1, pos2, pos3, pos4, pos5);

        return myLittleEvent;
    }

    /**
     * creates a litte test event ('My little event') with 2 domains D1 (positios: P1, P2) and D2 (positios: P3, P4,
     * P5). Every position is assigned to helper with a similar name (H1<->P1 etc. pp.). Every domain is lead by by the
     * first helper (H1 leads D1, H3 leads D2).
     * 
     * @param templateTri
     * @param finished
     * 
     * @param k
     * @param j
     * @param i
     * @param string2
     * @param string
     * @return
     */
    public static Event createSimpleEvent(String description, String eventKey, int day, int month, int year, EventState eventState, String templateName)
    {
        EventTemplate template = EntityFactory.buildEventTemplate("123ggg").saveOrUpdate();

        // build event
        Event myLittleEvent = EntityFactory.buildEvent(description, eventKey, day, month, year, EventState.FINISHED, template, null).saveOrUpdate();
        // create helpers
        Helper helper1 = EntityFactory.buildHelper("H1_First", "H1_Last", DEFAULT_MAIL_ADDRESS, HelperState.ACTIVE, 1, 2, 1980).saveOrUpdate();
        Helper helper2 = EntityFactory.buildHelper("H2_First", "H2_Last", DEFAULT_MAIL_ADDRESS, HelperState.ACTIVE, 2, 2, 1980).saveOrUpdate();
        Helper helper3 = EntityFactory.buildHelper("H3_First", "H3_Last", DEFAULT_MAIL_ADDRESS, HelperState.ACTIVE, 3, 2, 1980).saveOrUpdate();
        Helper helper4 = EntityFactory.buildHelper("H4_First", "H4_Last", DEFAULT_MAIL_ADDRESS, HelperState.ACTIVE, 4, 2, 1980).saveOrUpdate();
        Helper helper5 = EntityFactory.buildHelper("H5_First", "H5_Last", DEFAULT_MAIL_ADDRESS, HelperState.ACTIVE, 5, 2, 1980).saveOrUpdate();
        // build domains
        Domain domain1 = EntityFactory.buildDomain("D1", 1).saveOrUpdate();
        Domain domain2 = EntityFactory.buildDomain("D2", 2).saveOrUpdate();
        // build positions
        Position pos1 = EntityFactory.buildPosition("P1", 12, domain1, 0, true).saveOrUpdate();
        Position pos2 = EntityFactory.buildPosition("P2", 12, domain1, 1, true).saveOrUpdate();
        Position pos3 = EntityFactory.buildPosition("P3", 12, domain2, 2, true).saveOrUpdate();
        Position pos4 = EntityFactory.buildPosition("P4", 12, domain2, 3, true).saveOrUpdate();
        Position pos5 = EntityFactory.buildPosition("P5", 12, domain2, 4, true).saveOrUpdate();
        // assign positions to event
        SpeedyRoutines.relatePositionsToEvent(myLittleEvent, pos1, pos2, pos3, pos4, pos5);
        // assign helpers to positions
        SpeedyRoutines.assignHelperToPositions(helper1, myLittleEvent, pos1);
        SpeedyRoutines.assignHelperToPositions(helper2, myLittleEvent, pos2);
        SpeedyRoutines.assignHelperToPositions(helper3, myLittleEvent, pos3);
        SpeedyRoutines.assignHelperToPositions(helper4, myLittleEvent, pos4);
        SpeedyRoutines.assignHelperToPositions(helper5, myLittleEvent, pos5);

        return myLittleEvent;
    }

    public static Event createAggregationEvent(String description, String eventKey, int day, int month, int year, EventState eventState, String templateName,
            boolean doPriorize)
    {
        EventTemplate template = EntityFactory.buildEventTemplate("123ggg").saveOrUpdate();

        // build event
        Event event = EntityFactory.buildEvent(description, eventKey, day, month, year, EventState.FINISHED, template, null).saveOrUpdate();
        // build domains
        Domain domain1 = EntityFactory.buildDomain("D1", 1).saveOrUpdate();
        // build positions
        Position pos1 = EntityFactory.buildPosition("P1", 12, domain1, 0, true).saveOrUpdate();
        Position pos2 = EntityFactory.buildPosition("P2", 12, domain1, 1, true).saveOrUpdate();
        Position pos3 = EntityFactory.buildPosition("P3", 12, domain1, 2, true).saveOrUpdate();
        Position pos4 = EntityFactory.buildPosition("P4", 12, domain1, 3, true).saveOrUpdate();
        Position pos5 = EntityFactory.buildPosition("P5", 12, domain1, 4, true, doPriorize
                ? PRIO1
                : null).saveOrUpdate();
        Position pos6 = EntityFactory.buildPosition("P6", 12, domain1, 5, true, doPriorize
                ? PRIO1
                : null).saveOrUpdate();
        Position pos7 = EntityFactory.buildPosition("P7", 12, domain1, 6, true, doPriorize
                ? PRIO1
                : null).saveOrUpdate();
        Position pos8 = EntityFactory.buildPosition("P8", 12, domain1, 7, true, doPriorize
                ? PRIO1
                : null).saveOrUpdate();
        Position pos9 = EntityFactory.buildPosition("P9", 12, domain1, 8, true, doPriorize
                ? PRIO1
                : null).saveOrUpdate();
        Position pos10 = EntityFactory.buildPosition("P10", 12, domain1, 9, true, doPriorize
                ? PRIO2
                : null).saveOrUpdate();
        Position pos11 = EntityFactory.buildPosition("P11", 12, domain1, 10, true, doPriorize
                ? PRIO2
                : null).saveOrUpdate();
        Position pos12 = EntityFactory.buildPosition("P12", 12, domain1, 11, true, doPriorize
                ? PRIO2
                : null).saveOrUpdate();
        Position pos13 = EntityFactory.buildPosition("P13", 12, domain1, 12, true, doPriorize
                ? PRIO2
                : null).saveOrUpdate();

        // assign positions to event
        SpeedyRoutines.relatePositionsToEvent(event, pos1, pos2, pos3, pos4, pos5, pos6, pos7, pos8, pos9, pos10, pos11, pos12, pos13);

        return event;
    }

    /**
     * creates the minimal event : one domain, one position and one helper assigned to it.
     * 
     * @param description
     * @param eventKey
     * @param day
     * @param month
     * @param year
     * @param templateTri
     * @param finished
     * @return
     */
    public static Event createMinimalEvent(String description, String eventKey, int day, int month, int year, EventState eventState, String templateName)
    {
        EventTemplate template = EntityFactory.buildEventTemplate("123").saveOrUpdate();

        // build event
        Event myMinimalEvent = EntityFactory.buildEvent(description, eventKey, day, month, year, EventState.PLANNED, template, null).saveOrUpdate();
        // create helper
        Helper helper = EntityFactory.buildHelper("H1_First", "H1_Last", DEFAULT_MAIL_ADDRESS, HelperState.ACTIVE, 1, 1, 1980).saveOrUpdate();
        // build domain
        Domain domain = EntityFactory.buildDomain("D1", 787).saveOrUpdate();
        // build position
        Position pos = EntityFactory.buildPosition("P1", 12, domain, 53, true).saveOrUpdate();
        // assign position to event
        SpeedyRoutines.relatePositionsToEvent(myMinimalEvent, pos);
        // assign helper to position
        SpeedyRoutines.assignHelperToPositions(helper, myMinimalEvent, pos);
        return myMinimalEvent;
    }

    public static Event createRealLifeEvent(String description, String eventKey, int day, int month, int year, EventState eventState, String templateName)
    {
        // build event template
        EventTemplate template = EntityFactory.buildEventTemplate(templateName).saveOrUpdate();

        // build event
        Event event = EntityFactory.buildEvent(description, eventKey, day, month, year, eventState, template, null).saveOrUpdate();

        // ------------------------ create helpers ('old')
        EntityFactory.buildHelper("Schulz", "Stefan", DEFAULT_MAIL_ADDRESS, HelperState.ACTIVE, 13, 2, 2011).saveOrUpdate();
        EntityFactory.buildHelper("Beyer", "Lars", DEFAULT_MAIL_ADDRESS, HelperState.ACTIVE, 4, 4, 1971).saveOrUpdate();
        EntityFactory.buildHelper("Elsner", "Conny", DEFAULT_MAIL_ADDRESS, HelperState.ACTIVE, 25, 7, 1973).saveOrUpdate();
        EntityFactory.buildHelper("Deyhle", "Ingo", DEFAULT_MAIL_ADDRESS, HelperState.ACTIVE, 1, 8, 1968).saveOrUpdate();
        EntityFactory.buildHelper("Meitzner", "Daniela", DEFAULT_MAIL_ADDRESS, HelperState.ACTIVE, 16, 12, 1961).saveOrUpdate();
        EntityFactory.buildHelper("Grabbe", "Jimi", DEFAULT_MAIL_ADDRESS, HelperState.ACTIVE, 7, 5, 1991).saveOrUpdate();
        EntityFactory.buildHelper("Päge", "Denny", DEFAULT_MAIL_ADDRESS, HelperState.ACTIVE, 29, 5, 1964).saveOrUpdate();
        EntityFactory.buildHelper("Thierse", "Ulrich", DEFAULT_MAIL_ADDRESS, HelperState.ACTIVE, 16, 5, 1983).saveOrUpdate();
        EntityFactory.buildHelper("Müller", "Werner", DEFAULT_MAIL_ADDRESS, HelperState.ACTIVE, 22, 11, 1984).saveOrUpdate();
        EntityFactory.buildHelper("Unterberg", "Dorothea", DEFAULT_MAIL_ADDRESS, HelperState.ACTIVE, 23, 3, 1992).saveOrUpdate();

        // ------------------------ create event templates
        EntityFactory.buildEventTemplate("TriathlonTemplate").saveOrUpdate();
        EntityFactory.buildEventTemplate("CrosszalesTemplate").saveOrUpdate();

        // ------------------------ create triathlon 2015

        // Domain 'Laufstrecke'
        Domain domLauf = EntityFactory.buildDomain("Laufstrecke", 1).saveOrUpdate();
        Position posAnsageZieleinlauf = EntityFactory.buildPosition("Ansage Zieleinlauf", 12, domLauf, 0, true, PRIO1).saveOrUpdate();
        Position posVerpflegungPark = EntityFactory.buildPosition("Verpflegung Park", 12, domLauf, 1, true, PRIO2).saveOrUpdate();
        SpeedyRoutines.relatePositionsToEvent(event, posAnsageZieleinlauf, posVerpflegungPark);
        SpeedyRoutines.confirmHelperToPositions(RepositoryProvider.getRepository(HelperRepository.class).findByCode("SCST13022011"), event,
                posAnsageZieleinlauf);
        SpeedyRoutines.confirmHelperToPositions(RepositoryProvider.getRepository(HelperRepository.class).findByCode("BELA04041971"), event, posVerpflegungPark);

        // Domain 'Radstrecke'
        Domain domRad = EntityFactory.buildDomain("Radstrecke", 2).saveOrUpdate();
        Position posKontrolleAbstieg = EntityFactory.buildPosition("Kontrolle Abstieg", 12, domRad, 2, true).saveOrUpdate();
        Position posEinweisungNachStartnummerWZ = EntityFactory.buildPosition("Einweisung nach Startnummer WZ", 12, domRad, 137, true, PRIO2).saveOrUpdate();
        Position posSicherungAbzweigRunde = EntityFactory.buildPosition("Sicherung Abzweig Runde 2/Zieleinfahrt", 12, domRad, 398, true, PRIO1).saveOrUpdate();
        Position posMotorrad1 = EntityFactory.buildPosition("Motorrad 1", 12, domRad, 232, true).saveOrUpdate();
        SpeedyRoutines.relatePositionsToEvent(event, posKontrolleAbstieg, posEinweisungNachStartnummerWZ, posSicherungAbzweigRunde, posMotorrad1);
        SpeedyRoutines.confirmHelperToPositions(RepositoryProvider.getRepository(HelperRepository.class).findByCode("PADE29051964"), event, posKontrolleAbstieg);
        SpeedyRoutines.confirmHelperToPositions(RepositoryProvider.getRepository(HelperRepository.class).findByCode("ELCO25071973"), event,
                posEinweisungNachStartnummerWZ);
        SpeedyRoutines.confirmHelperToPositions(RepositoryProvider.getRepository(HelperRepository.class).findByCode("DEIN01081968"), event,
                posSicherungAbzweigRunde);
        SpeedyRoutines.confirmHelperToPositions(RepositoryProvider.getRepository(HelperRepository.class).findByCode("THUL16051983"), event, posMotorrad1);

        // Domain 'Zielverpflegung'
        Domain domZiel = EntityFactory.buildDomain("Zielverpflegung", 17).saveOrUpdate();
        Position posAusgabeGetraenke = EntityFactory.buildPosition("Ausgabe Getränke", 12, domZiel, 38, true).saveOrUpdate();
        Position posObstschneiden = EntityFactory.buildPosition("Obstschneiden", 12, domZiel, 39, true, PRIO1).saveOrUpdate();
        SpeedyRoutines.relatePositionsToEvent(event, posAusgabeGetraenke, posObstschneiden);
        SpeedyRoutines.confirmHelperToPositions(RepositoryProvider.getRepository(HelperRepository.class).findByCode("MEDA16121961"), event, posAusgabeGetraenke);
        SpeedyRoutines.confirmHelperToPositions(RepositoryProvider.getRepository(HelperRepository.class).findByCode("MUWE22111984"), event, posObstschneiden);

        // Domain 'Siegerehrung'
        Domain domSieger = EntityFactory.buildDomain("Siegerehrung", 92).saveOrUpdate();
        Position posModeration = EntityFactory.buildPosition("Moderation", 12, domSieger, 93, false).saveOrUpdate();
        Position posAnreichenUrkunden = EntityFactory.buildPosition("Anreichen Urkunden", 12, domSieger, 94, true, PRIO1).saveOrUpdate();
        SpeedyRoutines.relatePositionsToEvent(event, posModeration, posAnreichenUrkunden);
        SpeedyRoutines.confirmHelperToPositions(RepositoryProvider.getRepository(HelperRepository.class).findByCode("UNDO23031992"), event, posModeration);
        SpeedyRoutines.confirmHelperToPositions(RepositoryProvider.getRepository(HelperRepository.class).findByCode("GRJI07051991"), event,
                posAnreichenUrkunden);

        // do some grouping
        SpeedyRoutines.createPositionAggregation("some_group", posAnreichenUrkunden, posMotorrad1);

        return event;
    }
}