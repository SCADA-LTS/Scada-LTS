package utils;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class DataTestUtils {
    private final static String CHARS = "a,ą,b,c,ć,d,e,ę,f,g,h,i,j,k,l,ł,m,n,ń,o,ó,p,r,s,ś,t,u,w,y,z,ź,ż,A,Ą,B,C,Ć,D,E,Ę,F,G,H,I,J,K,L,Ł,M,N,Ń,O,Ó,P,R,S,Ś,T,U,W,Y,Z,Ź,Ż,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,Ä,Ö,Ü,ß,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,ä,ö,ü,ß,à,â,ç,é,è,ê,ë,î,ï,ô,ù,û,ü,ÿ,À,Â,Ç,É,È,Ê,Ë,Î,Ï,Ô,Ù,Û,Ü,Ÿ,ã,õ,á,é,í,ó,ú,â,ê,ô,à,ç,Ã,Õ,Á,É,Í,Ó,Ú,Â,Ê,Ô,À,Ç,ä,é,ë,Ä,É,Ë, ,    ,à,è,é,ì,î,ò,ó,ù,À,È,É,Ì,Î,Ò,Ó,Ù,ä,é,è,ë,ï,ö,ü,Ä,É,È,Ë,Ï,Ö,Ü,中,国,中,華,人,民,共,和,國,1,2,3,4,5,6,7,8,9,0,~,!,@,#,$,%,^,&,*,(,),_,+,`,-,=,[,],;,',\\,.,/,{,},:,\",|,<,>,?";

    public static String generateStringUtfRandom(long utfLength) {
        String result = "";
        while (result.getBytes(StandardCharsets.UTF_8).length != utfLength) {
            result = _generateStringUtfRandom(utfLength);
        }
        return result;
    }
    private static String _generateStringUtfRandom(long utfLengthMax) {
        StringBuilder result = new StringBuilder();
        String[] sp = CHARS.split(",");
        Random rand = new Random();
        long utfLen = 0;
        for(int i = 0; i < utfLengthMax; i++) {
            if(utfLen >= utfLengthMax - 1)
                break;
            String next = sp[rand.nextInt(sp.length)];
            utfLen = utfLen + next.getBytes(StandardCharsets.UTF_8).length;
            result.append(next);
        }
        return result.toString();
    }


}
