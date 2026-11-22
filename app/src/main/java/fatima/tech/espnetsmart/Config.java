package fatima.tech.espnetsmart;

public class Config {
    /** Conta SMTP do "Fale conosco": vem de local.properties (esplink.mail.user / esplink.mail.password), fora do Git. */
    public static final String EMAIL = BuildConfig.MAIL_USER;
    public static final String PASSWORD = BuildConfig.MAIL_PASSWORD;
    public static final String dummyEMAIL = "Support@FatimaTech.com";
    public static final String recpEMAIL = "zainul.aabdin@gmail.com";
}
