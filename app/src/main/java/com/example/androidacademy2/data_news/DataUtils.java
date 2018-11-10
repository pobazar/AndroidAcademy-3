package com.example.androidacademy2.data_news;

import java.util.ArrayList;
import java.util.List;

public class DataUtils  {

    public static List<NewsItem> generateNews() {
        final String travel = "Путешествия";
        final String sport =  "Спорт";
        final String economic =  "Экономика";
        final String science = "Наука";

        List<NewsItem> news = new ArrayList<>();
        news.add(new NewsItem(
                "Дальний рубеж",
                "https://icdn.lenta.ru/images/2018/10/05/13/20181005132054508/preview_da50b060653946ff8c48ed4536507a9d.jpg",
                travel,
                createDate(2018, 9, 19, 11, 43),
                "Они победили суровую природу и дошли до границы России и США",
                "Российская экспедиция на яхте Liberty добралась до Командорских " +
                        "островов в Беринговом море — места, где проходит морская граница Российской Федерации и США, " +
                        "а также международная линия перемены дат. Попасть на Командоры непросто. В единственный населенный пункт на крупнейшем из островов," +
                        " острове Беринга, — в село Никольское, — летают самолеты, но взлетная полоса на аэродроме грунтовая, и в плохую погоду он закрыт." +
                        " Причем, как отмечают местные жители, плохая погода там практически всегда.\n" +
                        "Море в районе островов бурное, часто бывают шторма. Из-за постоянных циклонов и нестабильной погоды путешествие на яхте до Командор — опасная затея." +
                        " Но все же 250 миль морем в одну сторону показались путешественникам более предпочтительным вариантом, нежели ожидание летной погоды в течение неопределенного времени." +
                        " Организатор экспедиции Nikon Special Project, фотограф Кирилл Умрихин, рассказал «Ленте.ру»," +
                        " чем замечателен этот затерянный уголок российской территории у самого обреза карты, и почему ради путешествия на Командоры стоит рискнуть.", ""
        ));


        return news;
    }

   /* private static Date createDate(int year, int month, int date, int hrs, int min) {
        return new GregorianCalendar(year, month - 1, date, hrs, min).getTime();
    }*/
   private static String createDate(int year, int month, int date, int hrs, int min) {
       return date+"."+month+"."+year+ " "+hrs+":"+min;
   }
}