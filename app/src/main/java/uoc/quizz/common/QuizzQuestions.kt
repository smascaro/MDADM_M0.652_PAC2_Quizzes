package uoc.quizz.common

import uoc.quizz.data.entity.Answer
import uoc.quizz.data.entity.Question

/**
 * Battery of questions
 */
object QuizzQuestions {
    val questions = listOf(
        Question(
            titleDefault = "Adivina el nombre del personaje",
            titleEn = "Guess the character's name",
            titleCa = "Endevina el nom del personatge",
            answers = listOf(
                Answer(1001, "Michael Scott"),
                Answer(1002, "Jim Halpert"),
                Answer(1003, "Dwight Schrute")
            ),
            rightAnswerId = 1001,
            imageUrl = "https://www.lavanguardia.com/r/GODO/LV/p7/WebSite/2020/06/29/Recortada/img_psola_20200629-193701_imagenes_lv_terceros_intro-1591207215-kccD-U482028786326yNI-992x558@LaVanguardia-Web.jpg"
        ),
        Question(
            titleEn = "Where is the office located?",
            titleDefault = "¿Dónde se encuentra la oficina?",
            titleCa = "On es troba la oficina?",
            answers = listOf(
                Answer(1101, "Nashua, NH"),
                Answer(1102, "Utica, NY"),
                Answer(1103, "Scranton, PA")
            ),
            rightAnswerId = 1103,
            imageUrl = "https://s28258.pcdn.co/wp-content/uploads/2020/02/Whimsy-Soul-Dunder-Mifflin-The-Office-3.png"
        ),
        Question(
            titleEn = "Who is he?",
            titleDefault = "¿Quien es?",
            titleCa = "Qui és?",
            answers = listOf(Answer(1201, "Kevin Malone"), Answer(1202, "Ashton Kutcher")),
            rightAnswerId = 1202,
            imageUrl = "https://images2.minutemediacdn.com/image/upload/c_fill,w_720,ar_16:9,f_auto,q_auto,g_auto/shape/cover/sport/dataimagepngbase64iVBORw0KGgoAAAANSUhEUgAAA60AAAHk-44fe9e2d2f237407b5e067bbdcb4d58e.jpg"
        ),
        Question(
            titleEn = "Guess the character's name",
            titleDefault = "Adivina el nombre del personaje",
            titleCa = "Endevina el nom del personatge",
            answers = listOf(
                Answer(1301, "Roy"),
                Answer(1302, "Bob Vance, Vance Refigeration"),
                Answer(1303, "Todd Packer")
            ),
            rightAnswerId = 1302,
            imageUrl = "https://images2.minutemediacdn.com/image/upload/c_fill,w_912,h_516,f_auto,q_auto,g_auto/shape/cover/entertainment/5ae37e2d1377c36a47000003.png"
        ),
        Question(
            titleEn = "Which Michael is he?",
            titleDefault = "¿De qué Michael se trata?",
            titleCa = "De quin Michael es tracta?",
            answers = listOf(
                Answer(1401, "Date Mike"),
                Answer(1402, "Prison Mike"),
                Answer(1403, "Michael Scarn")
            ),
            rightAnswerId = 1401,
            imageUrl = "https://i.redd.it/4ykhuontn1xz.jpg"
        ),
        Question(
            titleEn = "Guess the character's name",
            titleDefault = "Adivina el nombre del personaje",
            titleCa = "Endevina el nom del personatge",
            answers = listOf(
                Answer(1501, "Pam"),
                Answer(1502, "Angela"),
                Answer(1503, "Erin")
            ),
            rightAnswerId = 1502,
            imageUrl = "https://s.yimg.com/uu/api/res/1.2/a7PgvEtqTnI74ESFB8aJ8g--~B/aD0zMjg7dz01MDA7c209MTthcHBpZD15dGFjaHlvbg--/http://media.zenfs.com/en-US/homerun/hello_giggles_454/a786ff28b7506f3966b127ab2ea8c53d"
        ),
    )
}