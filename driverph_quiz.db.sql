BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "quiz_questions" (
	"_id"	INTEGER PRIMARY KEY AUTOINCREMENT,
	"question"	TEXT,
	"option1"	TEXT,
	"option2"	TEXT,
	"option3"	TEXT,
	"answer_nr"	INTEGER
);
CREATE TABLE IF NOT EXISTS "android_metadata" (
	"locale"	TEXT
);
INSERT INTO "quiz_questions" VALUES (1,'A is correct','A','B','C',1);
INSERT INTO "quiz_questions" VALUES (2,'B is correct','A','B','C',2);
INSERT INTO "quiz_questions" VALUES (3,'C is correct','A','B','C',3);
INSERT INTO "quiz_questions" VALUES (4,'A is correct again','A','B','C',1);
INSERT INTO "quiz_questions" VALUES (5,'B is correct again','A','B','C',2);
INSERT INTO "android_metadata" VALUES ('en_US');
COMMIT;
