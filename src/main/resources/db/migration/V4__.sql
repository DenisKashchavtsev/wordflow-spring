ALTER TABLE post_translation
    ADD keywords VARCHAR(500);

ALTER TABLE post_translation
    ADD meta_description VARCHAR(500);

ALTER TABLE post_translation
    ADD meta_title VARCHAR(255);

ALTER TABLE post
DROP
COLUMN content;

ALTER TABLE post
DROP
COLUMN keywords;

ALTER TABLE post
DROP
COLUMN meta_description;

ALTER TABLE post
DROP
COLUMN meta_title;

ALTER TABLE post
DROP
COLUMN title;