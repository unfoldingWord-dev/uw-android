package unfoldingword;

/**
 * Created by Fechner on 5/9/15.
 */
public class ModelNames {

    static public final int DB_VERSION_ID = 104;

    static public final String PROJECT = "Project";
    static public final String[] PROJECT_STRING_ATTRIBUTES = { "uniqueSlug", "slug", "title" };
    static public final String PROJECT_LANGUAGES_ATTRIBUTE = "languages";

    static public final String LANGUAGE = "Language";
    static public final String[] LANGUAGE_STRING_ATTRIBUTES = { "uniqueSlug", "slug", "languageAbbreviation" };
    static public final String[] LANGUAGE_DATE_ATTRIBUTES = { "modified" };
    static public final String LANGUAGE_PROJECT_ATTRIBUTE = "projectId";
    static public final String LANGUAGE_VERSIONS_ATTRIBUTE = "versions";

    static public final String VERSION = "Version";
    static public final String[] VERSION_STRING_ATTRIBUTES = { "uniqueSlug", "slug", "name", "statusCheckingEntity", "statusCheckingLevel",
            "statusComments", "statusContributors", "statusPublishDate", "statusSourceText", "statusSourceTextVersion", "statusVersion" };
    static public final String[] VERSION_DATE_ATTRIBUTES = { "modified" };
    static public final String VERSION_LANGUAGE_ATTRIBUTE = "languageId";
    static public final String VERSION_BOOKS_ATTRIBUTE = "books";

    static public final String BOOK = "Book";
    static public final String[] BOOK_STRING_ATTRIBUTES = { "uniqueSlug", "slug", "title", "description", "sourceUrl", "signatureUrl" };
    static public final String[] BOOK_DATE_ATTRIBUTES = { "modified" };
    static public final String BOOK_VERSION_ATTRIBUTE = "versionId";
    static public final String BOOK_VERIFICATIONS_ATTRIBUTE = "verifications";
    static public final String BOOK_BIBLE_CHAPTERS_ATTRIBUTE = "bibleChapters";
    static public final String BOOK_STORY_CHAPTERS_ATTRIBUTE = "storyChapters";
    static public final String BOOK_AUDIO_BOOK_ATTRIBUTE = "audioBookId";

    static public final String VERIFICATION = "Verification";
    static public final String[] VERIFICATION_STRING_ATTRIBUTES = { "signingInstitution","signature" };
    static public final String[] VERIFICATION_INT_ATTRIBUTES = { "status"};
    static public final String VERIFICATION_BOOK_ATTRIBUTE = "bookId";
    static public final String VERIFICATION_AUDIO_CHAPTER_ATTRIBUTE = "audioChapterId";

    static public final String BIBLE_CHAPTER = "BibleChapter";
    static public final String[] BIBLE_CHAPTER_STRING_ATTRIBUTES = { "uniqueSlug", "slug", "number", "text", "singleChapterBookName" };
    static public final String BIBLE_CHAPTER_BOOK_ATTRIBUTE = "bookId";

    static public final String STORY_CHAPTER = "StoriesChapter";
    static public final String[] STORY_CHAPTER_STRING_ATTRIBUTES = { "uniqueSlug", "slug","number", "title", "ref" };
    static public final String STORY_CHAPTER_BOOK_ATTRIBUTE = "bookId";
    static public final String STORY_CHAPTER_STORY_PAGES_ATTRIBUTE = "storyPages";

    static public final String STORY_PAGE = "StoryPage";
    static public final String[] STORY_PAGE_STRING_ATTRIBUTES = { "uniqueSlug", "slug","number", "text", "imageUrl" };
    static public final String STORY_PAGE_STORY_CHAPTER_ATTRIBUTE = "storyChapterId";

    static public final String LANGUAGE_LOCALE = "LanguageLocale";
    static public final String[] LANGUAGE_LOCALE_STRING_ATTRIBUTES = { "languageDirection","languageKey", "languageName", "cc", "LanguageRegion" };
    static public final String[] LANGUAGE_LOCALE_INT_ATTRIBUTES = { "pk"};
    static public final String[] LANGUAGE_LOCALE_BOOLEAN_ATTRIBUTES = { "gw"};

    static public final String SIGNING_ORGANIZATION = "SigningOrganization";
    static public final String[] SIGNING_ORGANIZATION_DATE_ATTRIBUTES = { "createdAt", "expiresAt", "modifiedAt" };
    static public final String[] SIGNING_ORGANIZATION_STRING_ATTRIBUTES = { "email","name", "url", "uniqueSlug", "slug" };

    static public final String AUDIO_BOOK = "AudioBook";
    static public final String[] AUDIO_BOOK_STRING_ATTRIBUTES = { "uniqueSlug", "contributors","revision", "textVersion" };
    static public final String AUDIO_BOOK_AUDIO_CHAPTER_ATTRIBUTE = "audioChapters";
    static public final String AUDIO_BOOK_BOOK_ATTRIBUTE = "bookId";

    static public final String AUDIO_CHAPTER = "AudioChapter";
    static public final String[] AUDIO_CHAPTER_STRING_ATTRIBUTES = {"bitrateJson", "uniqueSlug", "source","sourceSignature"};
    static public final String[] AUDIO_CHAPTER_INT_ATTRIBUTES = { "chapter", "length"};
    static public final String AUDIO_CHAPTER_AUDIO_BOOK_ATTRIBUTE = "audioBookId";
    static public final String AUDIO_CHAPTER_VERIFICATIONS_ATTRIBUTE = "verifications";
}
