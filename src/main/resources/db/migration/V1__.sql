CREATE TABLE bookmark
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    user_id    BIGINT,
    post_id    BIGINT                                  NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_bookmark PRIMARY KEY (id)
);

CREATE TABLE post
(
    id               BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    link             VARCHAR(255),
    image_url        VARCHAR(255),
    meta_title       VARCHAR(255),
    meta_description VARCHAR(500),
    keywords         VARCHAR(500),
    title            VARCHAR(255),
    content          VARCHAR(50000),
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_post PRIMARY KEY (id)
);

CREATE TABLE post_translation
(
    id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    level   VARCHAR(5),
    title   VARCHAR(255),
    content VARCHAR(50000),
    post_id BIGINT,
    CONSTRAINT pk_posttranslation PRIMARY KEY (id)
);

CREATE TABLE "user"
(
    id       BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    username VARCHAR(255),
    password VARCHAR(255),
    role     VARCHAR(50),
    CONSTRAINT pk_user PRIMARY KEY (id)
);

ALTER TABLE bookmark
    ADD CONSTRAINT FK_BOOKMARK_ON_POST FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE;

ALTER TABLE post_translation
    ADD CONSTRAINT FK_POSTTRANSLATION_ON_POST FOREIGN KEY (post_id) REFERENCES post (id) ON DELETE CASCADE;
