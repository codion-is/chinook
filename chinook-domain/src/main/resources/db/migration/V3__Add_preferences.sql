-- Add preferences table for storing user-specific settings
CREATE TABLE IF NOT EXISTS CHINOOK.PREFERENCES (
    CUSTOMER_ID LONG NOT NULL,
    PREFERRED_GENRE_ID LONG,
    NEWSLETTER_SUBSCRIBED BOOLEAN DEFAULT FALSE,
    CONSTRAINT PK_PREFERENCES PRIMARY KEY (CUSTOMER_ID),
    CONSTRAINT FK_PREFERENCES_CUSTOMER FOREIGN KEY (CUSTOMER_ID) REFERENCES CHINOOK.CUSTOMER(ID),
    CONSTRAINT FK_PREFERENCES_GENRE FOREIGN KEY (PREFERRED_GENRE_ID) REFERENCES CHINOOK.GENRE(ID)
);