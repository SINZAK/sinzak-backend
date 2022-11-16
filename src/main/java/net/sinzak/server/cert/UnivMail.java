package net.sinzak.server.cert;

public enum UnivMail {  /** 약 62개 **/

    GACHON("가천","gachon"), KANGWON("강원","kangwon"), KONKUK("건국","konkuk"), KKU("건국(글로컬)","kku"),
    GTEC("경기과학기술","gtec"),KYONGGI("경기","kyonggi"), KNU("경북","knu"), GINUE("경인교육","ginue"),
    KHU("경희","khu"), KAYWON("계원예술","kaywon"), KOREA("고려","korea"), KW("광운","kw"),
    KOOKMIN("국민","kookmin"),DANKOOK("단국","dankook"), DUKSUNG("덕성여자","duksung"), DONGGUK("동국","dongguk.edu"), DONGGUKK("동국(경주)","dongguk.ac.kr"),
    DONGDUK("동덕여자","dongduk"),MJU("명지","mju"), MJC("명지전문","mjc"), PUSAN("부산","pusan"), SANGMYUNG("상명","sangmyung"),
    SOGANG("서강","sogang"),SKUNIV("서경","skuniv"), SEOULTECH("서울과학기술","seoultech"), SNUE("서울교육","snue"),
    SNU("서울","snu"), UOS("서울시립","uos"), SWU("서울여자","swu"), SKKU("성균관","skku"),
    SUNGSHIN("성신여자","sungshin"),SJU("세종","sju"), SOOK("숙명여자","sookmyung"), SOONG("숭실","soongsil"),
    AJOU("아주","ajou"), YONSEI("연세","yonsei"), YNU("영남","ynu"), EWHA("이화여자","ewhain"),
    INU("인천","inu"),ITC("인하공전","itc"), INHA("인하","itc"), JNU("전남","jnu"),
    JBNU("전북","jbnu"), CAU("중앙","cau"), CHUNGBUK("충북","chungbuk"), KNOU("한국방송통신","knou"),
    KPU("한국산업기술","kpu"),KARTS("한국예술종합","karts"), HUFS("한국외국어","hufs"), KNSU("한국체육","knsu"),
    HANYANG("한양","hanyang"), ERICA("한양에리카","hanyang"), HONGIK("홍익","hongik"), DGIST("dgist","dgist"),
    GIST("gist","gist"),KAIST("카이스트","kaist"), POSTECH("포항공과","postech"), UNIST("unist","unist");


    private String name;
    private String mail;

    UnivMail(String name, String mail) {
        this.name = name;
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }

    public static String changeUnivToMail(String mail){
        UnivMail[] univMails = UnivMail.values();
        for (UnivMail univMail : univMails) {
            if(univMail.name.equals(mail))
                return univMail.mail;
        }
        return "";
    }
    public static String changeMailToUniv(String mail){ //insi2000@mail.hongik.ac.kr
        UnivMail[] univMails = UnivMail.values();
        String[] domain = mail.split("@",2); // domain[1]에 메일 도메인 존재

        for (UnivMail univMail : univMails) {
            if(domain[1].contains(univMail.mail))
                return univMail.name;
        }
        return "";
    }
}
