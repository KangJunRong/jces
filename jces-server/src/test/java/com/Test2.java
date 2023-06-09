package com;


import com.ecp.jces.core.utils.AesUtil2;

import java.io.IOException;
import java.net.URLDecoder;

public class Test2 {

    public static void main(String[] args) throws IOException {

        System.out.println(URLDecoder.decode((AesUtil2.decryptAES2("3fd839573600bcbd78a3e0d56a8c3ffccc825924a4196a53da2feabe819d03f92bd7e0de94764cffc03847092952bd039ef8110f9f164f3d1da4a59cbfb0bc343357385ac5c0ebc6e6d53045d5b3a9e90eef302c5b3e9e0eda3269488c06073f"))));
    }

}
