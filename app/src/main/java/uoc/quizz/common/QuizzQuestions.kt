package uoc.quizz.common

import android.content.Context

object QuizzQuestions {
    val questions: List<Question> = listOf(
        Question(
            "Guess the character's name",
            listOf(
                Answer(1001, "Michael Scott"),
                Answer(1002, "Jim Halpert"),
                Answer(1003, "Dwight Schrute")
            ),
            1001,
            "https://www.lavanguardia.com/r/GODO/LV/p7/WebSite/2020/06/29/Recortada/img_psola_20200629-193701_imagenes_lv_terceros_intro-1591207215-kccD-U482028786326yNI-992x558@LaVanguardia-Web.jpg"
        ),
        Question(
            "Where is the main office located?",
            listOf(
                Answer(1101, "Nashua, NH"),
                Answer(1102, "Utica, NY"),
                Answer(1103, "Scranton, PA")
            ),
            1103,
            "https://s28258.pcdn.co/wp-content/uploads/2020/02/Whimsy-Soul-Dunder-Mifflin-The-Office-3.png"
        ),
        Question(
            "Who is he?",
            listOf(Answer(1201, "Kevin Malone"), Answer(1202, "Ashton Kutcher")),
            1202,
            "https://i.pinimg.com/originals/36/df/d0/36dfd0f8a361c938603cfd1db6930ab2.jpg"
        ),
        Question(
            "Guess the character's name",
            listOf(
                Answer(1301, "Roy"),
                Answer(1302, "Bob Vance, Vance Refigeration"),
                Answer(1303, "Todd Packer")
            ),
            1302,
            "https://lh3.googleusercontent.com/proxy/IA6y1MgQ5ZkGCikJZGOzh8X95XhBURv1uLFTOaAcPnisDhNA54ffWB6Yp06mD03TRZjiMgipt65LSORqE_O1el3Jf4g3dMqafsTykGoyg-k2t7BKLQiDj8Lo_eV39g"
        ),
        Question(
            "Which Michael is it?",
            listOf(
                Answer(1401, "Date Mike"),
                Answer(1402, "Prison Mike"),
                Answer(1403, "Michael Scarn")
            ),
            1401,
            "https://i.redd.it/4ykhuontn1xz.jpg"
        ),
        Question(
            "Guess the character's name",
            listOf(
                Answer(1501, "Pam"),
                Answer(1502, "Angela"),
                Answer(1503, "Erin")
            ),
            1502,
            "https://s.yimg.com/uu/api/res/1.2/a7PgvEtqTnI74ESFB8aJ8g--~B/aD0zMjg7dz01MDA7c209MTthcHBpZD15dGFjaHlvbg--/http://media.zenfs.com/en-US/homerun/hello_giggles_454/a786ff28b7506f3966b127ab2ea8c53d"
        ),
    )
}