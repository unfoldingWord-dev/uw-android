package unfoldingword;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    private static final String UW_DATABASE_MODEL_PROTOCOL = "model.UWDatabaseModel";

    private static final String COMPARABLE_INTERFACE_BEGINNING = "Comparable<";
    private static final String COMPARABLE_INTERFACE_END = ">";

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(ModelNames.DB_VERSION_ID, "model.daoModels");

        schema.enableKeepSectionsByDefault();
        setupData(schema);

        new DaoGenerator().generateAll(schema, args[0]);
    }

    private static void setupData(Schema schema){

        createLanguageLocale(schema);

        Entity project = createProject(schema);
        Entity language = createLanguage(schema, project);
        Entity version = createVersion(schema, language);
        Entity book = createBook(schema, version);
        createBibleChapter(schema, book);
        Entity storyChapter = createStoryChapter(schema, book);
        createStoryPage(schema, storyChapter);
        createSigningOrganization(schema);
        Entity audioBook = createAudioBook(schema, book);
        Entity audioChapter = createAudioChapter(schema, audioBook);
        createVerification(schema, book, audioChapter);
    }

    private static Entity createProject(Schema schema) {

        DaoHelperMethods.EntityInformation projectInfo =
                new DaoHelperMethods.EntityInformation(ModelNames.PROJECT, ModelNames.PROJECT_STRING_ATTRIBUTES);
        Entity project = DaoHelperMethods.createEntity(schema, projectInfo);
        project.setSuperclass(UW_DATABASE_MODEL_PROTOCOL);
        return project;
    }

    private static Entity createLanguage(Schema schema, Entity project) {

        DaoHelperMethods.EntityInformation languageInfo =
                new DaoHelperMethods.EntityInformation(ModelNames.LANGUAGE, ModelNames.LANGUAGE_STRING_ATTRIBUTES,
                        ModelNames.LANGUAGE_DATE_ATTRIBUTES);
        Entity language = DaoHelperMethods.createEntity(schema, languageInfo);
        language.setSuperclass(UW_DATABASE_MODEL_PROTOCOL);
        DaoHelperMethods.createParentChildRelationship(
                project, ModelNames.PROJECT_LANGUAGES_ATTRIBUTE,
                language, ModelNames.LANGUAGE_PROJECT_ATTRIBUTE);

        return language;
    }

    private static Entity createVersion(Schema schema, Entity language) {

        DaoHelperMethods.EntityInformation versionInfo =
                new DaoHelperMethods.EntityInformation(ModelNames.VERSION, ModelNames.VERSION_STRING_ATTRIBUTES,
                        ModelNames.VERSION_DATE_ATTRIBUTES, ModelNames.VERSION_INT_ATTRIBUTES);
        Entity version = DaoHelperMethods.createEntity(schema, versionInfo);
        version.setSuperclass(UW_DATABASE_MODEL_PROTOCOL);
        DaoHelperMethods.createParentChildRelationship(
                language, ModelNames.LANGUAGE_VERSIONS_ATTRIBUTE,
                version, ModelNames.VERSION_LANGUAGE_ATTRIBUTE);

        return version;
    }

    private static Entity createBook(Schema schema, Entity version) {

        DaoHelperMethods.EntityInformation bookInfo =
                new DaoHelperMethods.EntityInformation(ModelNames.BOOK, ModelNames.BOOK_STRING_ATTRIBUTES,
                        ModelNames.BOOK_DATE_ATTRIBUTES);
        bookInfo.setBooleanAttributes(ModelNames.BOOK_BOOLEAN_ATTRIBUTES);
        Entity book = DaoHelperMethods.createEntity(schema, bookInfo);
        book.setSuperclass(UW_DATABASE_MODEL_PROTOCOL);
        DaoHelperMethods.createParentChildRelationship(
                version, ModelNames.VERSION_BOOKS_ATTRIBUTE,
                book, ModelNames.BOOK_VERSION_ATTRIBUTE);

        return book;
    }

    private static void createVerification(Schema schema, Entity book, Entity audioChapter) {

        DaoHelperMethods.EntityInformation verificationInfo =
                new DaoHelperMethods.EntityInformation(ModelNames.VERIFICATION, ModelNames.VERIFICATION_STRING_ATTRIBUTES);
        verificationInfo.intAttributes = ModelNames.VERIFICATION_INT_ATTRIBUTES;
        Entity verification = DaoHelperMethods.createEntity(schema, verificationInfo);
        verification.setSuperclass(UW_DATABASE_MODEL_PROTOCOL);

        DaoHelperMethods.createParentChildRelationship(
                book, ModelNames.BOOK_VERIFICATIONS_ATTRIBUTE,
                verification, ModelNames.VERIFICATION_BOOK_ATTRIBUTE);

        DaoHelperMethods.createParentChildRelationship(
                audioChapter, ModelNames.AUDIO_CHAPTER_VERIFICATIONS_ATTRIBUTE,
                verification, ModelNames.VERIFICATION_AUDIO_CHAPTER_ATTRIBUTE);
    }

    private static void createBibleChapter(Schema schema, Entity book) {

        DaoHelperMethods.EntityInformation bibleChapterInfo =
                new DaoHelperMethods.EntityInformation(ModelNames.BIBLE_CHAPTER, ModelNames.BIBLE_CHAPTER_STRING_ATTRIBUTES);
        Entity bibleChapter =  DaoHelperMethods.createEntity(schema, bibleChapterInfo);
        bibleChapter.setSuperclass(UW_DATABASE_MODEL_PROTOCOL);
        bibleChapter.implementsInterface(getComparableInterfaceForClassName(ModelNames.BIBLE_CHAPTER));

        DaoHelperMethods.createParentChildRelationship(
                book, ModelNames.BOOK_BIBLE_CHAPTERS_ATTRIBUTE,
                bibleChapter, ModelNames.BIBLE_CHAPTER_BOOK_ATTRIBUTE);
    }

    private static Entity createStoryChapter(Schema schema, Entity book) {

        DaoHelperMethods.EntityInformation storyChapterInfo =
                new DaoHelperMethods.EntityInformation(ModelNames.STORY_CHAPTER, ModelNames.STORY_CHAPTER_STRING_ATTRIBUTES);
        Entity storyChapter = DaoHelperMethods.createEntity(schema, storyChapterInfo);
        storyChapter.setSuperclass(UW_DATABASE_MODEL_PROTOCOL);
        storyChapter.implementsInterface(getComparableInterfaceForClassName(ModelNames.STORY_CHAPTER));

        DaoHelperMethods.createParentChildRelationship(
                book, ModelNames.BOOK_STORY_CHAPTERS_ATTRIBUTE,
                storyChapter, ModelNames.STORY_CHAPTER_BOOK_ATTRIBUTE);

        return storyChapter;
    }

    private static void createStoryPage(Schema schema, Entity storyChapter) {

        DaoHelperMethods.EntityInformation storyChapterPage =
                new DaoHelperMethods.EntityInformation(ModelNames.STORY_PAGE, ModelNames.STORY_PAGE_STRING_ATTRIBUTES);
        Entity storyPage = DaoHelperMethods.createEntity(schema, storyChapterPage);
        storyPage.setSuperclass(UW_DATABASE_MODEL_PROTOCOL);

        DaoHelperMethods.createParentChildRelationship(
                storyChapter, ModelNames.STORY_CHAPTER_STORY_PAGES_ATTRIBUTE,
                storyPage, ModelNames.STORY_PAGE_STORY_CHAPTER_ATTRIBUTE);
    }

    private static void createLanguageLocale(Schema schema) {

        DaoHelperMethods.EntityInformation languageLocaleInfo =
                new DaoHelperMethods.EntityInformation(ModelNames.LANGUAGE_LOCALE, ModelNames.LANGUAGE_LOCALE_STRING_ATTRIBUTES);
        languageLocaleInfo.booleanAttributes = ModelNames.LANGUAGE_LOCALE_BOOLEAN_ATTRIBUTES;
        languageLocaleInfo.intAttributes = ModelNames.LANGUAGE_LOCALE_INT_ATTRIBUTES;
        Entity languageLocal = DaoHelperMethods.createEntity(schema, languageLocaleInfo);
        languageLocal.setSuperclass(UW_DATABASE_MODEL_PROTOCOL);
    }

    private static void createSigningOrganization(Schema schema) {

        DaoHelperMethods.EntityInformation verification =
                new DaoHelperMethods.EntityInformation(ModelNames.SIGNING_ORGANIZATION, ModelNames.SIGNING_ORGANIZATION_STRING_ATTRIBUTES,
                        ModelNames.SIGNING_ORGANIZATION_DATE_ATTRIBUTES);

        Entity verificationEntity = DaoHelperMethods.createEntity(schema, verification);
    }

    private static Entity createAudioBook(Schema schema, Entity book) {

        DaoHelperMethods.EntityInformation audioBookInfo =
                new DaoHelperMethods.EntityInformation(ModelNames.AUDIO_BOOK, ModelNames.AUDIO_BOOK_STRING_ATTRIBUTES);
        Entity audioBook = DaoHelperMethods.createEntity(schema, audioBookInfo);
        audioBook.setSuperclass(UW_DATABASE_MODEL_PROTOCOL);

        DaoHelperMethods.createOneToOneRelationship(
                book, ModelNames.BOOK_AUDIO_BOOK_ATTRIBUTE,
                audioBook, ModelNames.AUDIO_BOOK_BOOK_ATTRIBUTE);

        return audioBook;
    }

    private static Entity createAudioChapter(Schema schema, Entity audioBook) {

        DaoHelperMethods.EntityInformation audioChapterInfo =
                new DaoHelperMethods.EntityInformation(ModelNames.AUDIO_CHAPTER, ModelNames.AUDIO_CHAPTER_STRING_ATTRIBUTES, ModelNames.AUDIO_CHAPTER_DATE_ATTRIBUTES,  ModelNames.AUDIO_CHAPTER_INT_ATTRIBUTES);
        Entity audioChapter = DaoHelperMethods.createEntity(schema, audioChapterInfo);
        audioChapter.setSuperclass(UW_DATABASE_MODEL_PROTOCOL);
        audioChapter.implementsInterface(getComparableInterfaceForClassName(ModelNames.AUDIO_CHAPTER));

        DaoHelperMethods.createParentChildRelationship(
                audioBook, ModelNames.AUDIO_BOOK_AUDIO_CHAPTER_ATTRIBUTE,
                audioChapter, ModelNames.AUDIO_CHAPTER_AUDIO_BOOK_ATTRIBUTE);

        return audioChapter;
    }

    private static String getComparableInterfaceForClassName(String className){

        return COMPARABLE_INTERFACE_BEGINNING + className + COMPARABLE_INTERFACE_END;
    }
}
