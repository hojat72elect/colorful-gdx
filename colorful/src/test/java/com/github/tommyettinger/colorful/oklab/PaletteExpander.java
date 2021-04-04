package com.github.tommyettinger.colorful.oklab;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.NumberUtils;
import com.github.tommyettinger.colorful.TrigTools;
import com.github.tommyettinger.colorful.internal.StringKit;

/*
"surin", based on azurestar
{
0x00000000, 0x000000FF, 0x141414FF, 0xFFFFFFFF, 0x878787FF, 0xCCCCCCFF, 0x4F4F4FFF, 0xEEEEEEFF,
0x282828FF, 0x999999FF, 0x757575FF, 0xDDDDDDFF, 0x3B3B3BFF, 0xBBBBBBFF, 0x626262FF, 0xAAAAAAFF,
0x9B9783FF, 0x514E3DFF, 0xE10788FF, 0xDC8EA1FF, 0xA55F72FF, 0x5E2637FF, 0xF7419BFF, 0x9E0C5BFF,
0x7C706BFF, 0x39302DFF, 0x943907FF, 0xAA755EFF, 0x794B37FF, 0x3A1706FF, 0xAA512AFF, 0x602400FF,
0xE09A8BFF, 0x884F44FF, 0xDC6D00FF, 0xFDAA7CFF, 0xE06719FF, 0x7C3603FF, 0xFF811DFF, 0xA15013FF,
0xECA992FF, 0x945D4AFF, 0xFF6B03FF, 0xFFBE9EFF, 0xEE763DFF, 0x913C07FF, 0xFF9664FF, 0xC15000FF,
0xC09C84FF, 0x6F523EFF, 0xFF3107FF, 0xFF9779FF, 0xCD633FFF, 0x7E2400FF, 0xFF6B4EFF, 0xB52800FF,
0xE9BB63FF, 0x916A13FF, 0xFF6B8EFF, 0xFFC8A5FF, 0xD78C59FF, 0x894A1BFF, 0xFE9C96FF, 0xC74F4EFF,
0xCAC0B2FF, 0x797165FF, 0xFF6F84FF, 0xFFC4C4FF, 0xDA878AFF, 0x8B464AFF, 0xFF9BA3FF, 0xCC475EFF,
0xEEBC98FF, 0x956C4DFF, 0xFF7C64FF, 0xFFCEBDFF, 0xDB9071FF, 0x8D4E33FF, 0xFFA68FFF, 0xC35E47FF,
0xE6CAB3FF, 0x917965FF, 0xF88D7EFF, 0xFEDBCCFF, 0xD79F8CFF, 0x8A5B4BFF, 0xFEB5A5FF, 0xBC6E60FF,
0xE7D7A1FF, 0x928355FF, 0xE79BBAFF, 0xFEE5DAFF, 0xD3AA99FF, 0x876556FF, 0xFCBBC3FF, 0xB27A81FF,
0xBCD68FFF, 0x6C8145FF, 0xF767FDFF, 0xE5DBCCFF, 0xAEA698FF, 0x696156FF, 0xEDA7E5FF, 0xA5689EFF,
0x6A945EFF, 0x294A1FFF, 0x9D00E1FF, 0xAC80D6FF, 0x7B53A0FF, 0x3E1B5BFF, 0xB335FFFF, 0x690B99FF,
0x7AAA6AFF, 0x365E28FF, 0xB725FFFF, 0xD08BFFFF, 0x995ACFFF, 0x561A81FF, 0xC261FFFF, 0x830CBDFF,
0x507A5EFF, 0x133722FF, 0x6410D3FF, 0x6E78BCFF, 0x464C88FF, 0x181747FF, 0x6D45DEFF, 0x3D008CFF,
0x739B83FF, 0x2F513EFF, 0x7B44FFFF, 0x849EE5FF, 0x586EADFF, 0x233266FF, 0x866EFFFF, 0x5203DCFF,
0x7FB98AFF, 0x386A43FF, 0xA162FFFF, 0xA5B9EAFF, 0x7688B5FF, 0x3A486DFF, 0xAB8BFFFF, 0x6D40D3FF,
0x90CCAFFF, 0x457961FF, 0x958AFDFF, 0xB6D2FDFF, 0x7B9AD8FF, 0x3D578BFF, 0xA6ACFFFF, 0x6264DDFF,
0x508B6BFF, 0x0C442CFF, 0x6A10FDFF, 0x6191B8FF, 0x386487FF, 0x002B46FF, 0x635EEBFF, 0x3622A1FF,
0x659FADFF, 0x21535FFF, 0x0080BAFF, 0x4FB3DBFF, 0x1B81A5FF, 0x103E53FF, 0x139AD5FF, 0x065A7DFF,
0x94BAD4FF, 0x4B6B81FF, 0x00AAC5FF, 0x5EDBFFFF, 0x25A5C9FF, 0x055D70FF, 0x26C3E6FF, 0x057D95FF,
0x9DC1EFFF, 0x527198FF, 0x1CB6B9FF, 0x6EE6FFFF, 0x10B3CFFF, 0x026674FF, 0x1FD1E0FF, 0x00898FFF,
0x707DD4FF, 0x32387EFF, 0x197A62FF, 0x6898D8FF, 0x3E6AA3FF, 0x072E5DFF, 0x278DA1FF, 0x114F5AFF,
0x869BEFFF, 0x425196FF, 0x179982FF, 0x5CC1E9FF, 0x2B8FB4FF, 0x114A5FFF, 0x00B3B0FF, 0x116D6BFF,
0xB898E7FF, 0x694D8FFF, 0x00AA3BFF, 0x92C7C5FF, 0x629492FF, 0x275250FF, 0x14C560FF, 0x127A3BFF,
0xBD87D6FF, 0x6C3F80FF, 0x4F961CFF, 0x9CBA97FF, 0x6D8868FF, 0x32482EFF, 0x55B31FFF, 0x386C13FF,
0xD3B0E3FF, 0x7F628CFF, 0x5FBA18FF, 0xA6E790FF, 0x75B060FF, 0x376923FF, 0x6DD819FF, 0x468C0DFF,
0x98738BFF, 0x4D3144FF, 0x65690FFF, 0x9D8E71FF, 0x6E6147FF, 0x342915FF, 0x827C31FF, 0x4A4406FF,
0xD47AA4FF, 0x7D3458FF, 0x8E7C13FF, 0xDC9B71FF, 0xA56B45FF, 0x5F300CFF, 0xB88C14FF, 0x6E530AFF,
0xDBA4AAFF, 0x86595EFF, 0xC98800FF, 0xF8B690FF, 0xC08562FF, 0x764527FF, 0xF79617FF, 0x9D600EFF,
0xEAB0CAFF, 0x926278FF, 0xBAA300FF, 0xFDD07DFF, 0xC7993CFF, 0x7B5800FF, 0xDCBA1CFF, 0x917911FF,
}

A different try, without a palette as a base, "enclave":
0x00000000, 0x000000FF, 0x141414FF, 0xFFFFFFFF, 0x878787FF, 0xCCCCCCFF, 0x4F4F4FFF, 0xEEEEEEFF,
0x282828FF, 0x999999FF, 0x757575FF, 0xDDDDDDFF, 0x3B3B3BFF, 0xBBBBBBFF, 0x626262FF, 0xAAAAAAFF,
0xD091A6FF, 0xFC74A4FF, 0xFEE4EEFF, 0xDD8AA6FF, 0x75354CFF, 0xFCCCDCFF, 0xE982A5FF, 0x964060FF,
0xAE707AFF, 0xFF135BFF, 0xFF9EADFF, 0xCB5E6EFF, 0x620923FF, 0xFF7395FF, 0xE74367FF, 0x90002DFF,
0xC58783FF, 0xFF605EFF, 0xFFD1CDFF, 0xCF827DFF, 0x692F2DFF, 0xFFA5A0FF, 0xE67471FF, 0x923334FF,
0xCC8F7EFF, 0xFF7150FF, 0xFEDDD4FF, 0xD88972FF, 0x703423FF, 0xFFC5B6FF, 0xEB7E65FF, 0x973C28FF,
0xC1886AFF, 0xF96716FF, 0xFFD0B7FF, 0xD1805DFF, 0x6A2C0FFF, 0xFFA76FFF, 0xE37641FF, 0x903400FF,
0xC2926AFF, 0xED7E0CFF, 0xFFD49CFF, 0xCA8F5DFF, 0x67390AFF, 0xFFBB76FF, 0xD88948FF, 0x884600FF,
0xA78353FF, 0xC27900FF, 0xF7C382FF, 0xAF8046FF, 0x522E00FF, 0xEFAD5CFF, 0xB87D2FFF, 0x6E3C00FF,
0xA58D59FF, 0xB9880EFF, 0xF0D189FF, 0xA98D4CFF, 0x4E3800FF, 0xE4BD63FF, 0xAF8B35FF, 0x674900FF,
0x938852FF, 0xA18615FF, 0xDACB88FF, 0x95884CFF, 0x403500FF, 0xCEB862FF, 0x9B8736FF, 0x574600FF,
0x909258FF, 0x98930AFF, 0xD3D888FF, 0x90944BFF, 0x3C3D00FF, 0xC3C761FF, 0x919434FF, 0x505100FF,
0x99AC72FF, 0x99B200FF, 0xDDF6A5FF, 0x98AE65FF, 0x425113FF, 0xCAE577FF, 0x97B049FF, 0x556800FF,
0x6B8D5AFF, 0x5A960CFF, 0xA7D390FF, 0x688F54FF, 0x1D3A07FF, 0x8CC56CFF, 0x5E9341FF, 0x244F00FF,
0x7EAF82FF, 0x00C62BFF, 0xA2FFABFF, 0x61B96BFF, 0x00591AFF, 0x75F77EFF, 0x43C050FF, 0x007400FF,
0x538E6EFF, 0x089A59FF, 0x85D6A7FF, 0x489169FF, 0x003B1FFF, 0x60C991FF, 0x309664FF, 0x00512AFF,
0x5AA68DFF, 0x00B084FF, 0x8AF1D1FF, 0x49A98EFF, 0x004D3AFF, 0x6BE1BAFF, 0x39AC89FF, 0x006448FF,
0x59AC9FFF, 0x00B5A0FF, 0x8AF8E5FF, 0x48AF9FFF, 0x005247FF, 0x67E8D3FF, 0x31B2A0FF, 0x006A5BFF,
0x4BA3A1FF, 0x08A9A7FF, 0x83EBE7FF, 0x41A4A1FF, 0x004A48FF, 0x65DADAFF, 0x32A6A6FF, 0x005F60FF,
0x53A7B2FF, 0x17ADBDFF, 0x80F2FFFF, 0x3CAAB7FF, 0x004E5AFF, 0x61E1F3FF, 0x2BACBDFF, 0x006473FF,
0x4392A8FF, 0x1496B3FF, 0x77D8F6FF, 0x3793ADFF, 0x003C51FF, 0x5AC7E8FF, 0x2794B3FF, 0x00506AFF,
0x4B96B9FF, 0x009ACFFF, 0x82DDFFFF, 0x4398B9FF, 0x00405AFF, 0x63CCFBFF, 0x3298C4FF, 0x005478FF,
0x508AB4FF, 0x218BD3FF, 0x81CFFFFF, 0x438ABEFF, 0x00355DFF, 0x63BDFFFF, 0x358BC9FF, 0x00487CFF,
0x608CBFFF, 0x198AFFFF, 0x91D1FFFF, 0x538DCFFF, 0x00366AFF, 0x67C0FFFF, 0x398DE4FF, 0x004993FF,
0x809ED7FF, 0x729BFDFF, 0xD6E4FFFF, 0x7E9EDCFF, 0x2E4576FF, 0xA8D0FFFF, 0x789DEDFF, 0x3A579BFF,
0x92A1DEFF, 0x8C9CFEFF, 0xE6E9FFFF, 0x8EA0E9FF, 0x3A4680FF, 0xCED8FFFF, 0x8A9FF4FF, 0x4A59A1FF,
0x9394D3FF, 0x9887FFFF, 0xDDDDFFFF, 0x9592DEFF, 0x413B77FF, 0xC5C0FFFF, 0x938DF3FF, 0x5249A0FF,
0xA697D5FF, 0xB586FEFF, 0xEEE2FDFF, 0xA794DFFF, 0x4E3D78FF, 0xE0C1FFFF, 0xAB8FEFFF, 0x664B9DFF,
0x9D81BAFF, 0xC254FFFF, 0xF0B8FFFF, 0xA877CEFF, 0x4E2569FF, 0xEA97FFFF, 0xB467EBFF, 0x6C2399FF,
0xAB85B6FF, 0xEB38FFFF, 0xFFBBFFFF, 0xBC79C4FF, 0x5B2763FF, 0xFF95FFFF, 0xCF64E2FF, 0x801F91FF,
0xAA7BA2FF, 0xF806D4FF, 0xFFB0F3FF, 0xBB6FABFF, 0x591F50FF, 0xFF85F4FF, 0xD655BEFF, 0x840873FF,
0xB87F9EFF, 0xFF36AEFF, 0xFFAFEEFF, 0xD06EA7FF, 0x691B4DFF, 0xFF89DFFF, 0xE859AAFF, 0x930E64FF,

"wagon240" based on resurrect32 with some small changes:
0x00000000, 0x000000FF, 0x111111FF, 0x222222FF, 0x333333FF, 0x444444FF, 0x555555FF, 0x666666FF,
0x777777FF, 0x888888FF, 0x999999FF, 0xAAAAAAFF, 0xBBBBBBFF, 0xCCCCCCFF, 0xDDDDDDFF, 0xEEEEEEFF,
0xFFFFFFFF, 0x862100FF, 0xAA2B00FF, 0xC73200FF, 0xE63800FF, 0xFF3E00FF, 0xFF5C00FF, 0xFF8031FF,
0xFFA06CFF, 0x9F001CFF, 0xD10020FF, 0xF90027FF, 0xFF002DFF, 0xFF003AFF, 0xFE456BFF, 0xFF6981FF,
0xFF8998FF, 0x4B0A2CFF, 0x6B0B3CFF, 0x860D4AFF, 0xA10D58FF, 0xBB0B6AFF, 0xDA0879FF, 0xF90088FF,
0xFF4099FF, 0x640930FF, 0x860941FF, 0xA30A4FFF, 0xC1095DFF, 0xDB146BFF, 0xFB137BFF, 0xFF498BFF,
0xFF709DFF, 0x800D3EFF, 0xA50D50FF, 0xC20C5EFF, 0xE10B6DFF, 0xFF077DFF, 0xFF508EFF, 0xFF75A0FF,
0xFF94B2FF, 0x9E003DFF, 0xC9004AFF, 0xEA0059FF, 0xFF0062FF, 0xFF0070FF, 0xFE7191FF, 0xFD90A8FF,
0xFEAFBCFF, 0x962F2EFF, 0xBC393BFF, 0xDF3D3DFF, 0xF94B4CFF, 0xFF5353FF, 0xFF716BFF, 0xFF9A8FFF,
0xFFBAB4FF, 0x735811FF, 0x8D6C13FF, 0xA47D0AFF, 0xB88C14FF, 0xCD9E1EFF, 0xE4AE10FF, 0xFBC01DFF,
0xFFD672FF, 0x5C4300FF, 0x795400FF, 0x8D6200FF, 0xA37100FF, 0xB2831AFF, 0xBD9548FF, 0xC2AA73FF,
0xD0BC90FF, 0x5F2336FF, 0x7E2D47FF, 0x983656FF, 0xB23F65FF, 0xCD466EFF, 0xE6547EFF, 0xFF5C8EFF,
0xFF669EFF, 0x2D2C3EFF, 0x3D3C55FF, 0x4D4868FF, 0x5C5779FF, 0x6E648EFF, 0x7D739FFF, 0x8B82B5FF,
0x9992CCFF, 0x0E4341FF, 0x0E5A55FF, 0x146A64FF, 0x1B7C74FF, 0x0D9085FF, 0x14A296FF, 0x1BB5A7FF,
0x00D9CCFF, 0x095744FF, 0x006E58FF, 0x158062FF, 0x1D9372FF, 0x0AA883FF, 0x13BB93FF, 0x1ACFA4FF,
0x00F6ABFF, 0x256A0DFF, 0x31820FFF, 0x33981AFF, 0x3AAD12FF, 0x00D200FF, 0x00DD00FF, 0x62E64DFF,
0x93F085FF, 0x4C7C0BFF, 0x4C990CFF, 0x59AE1AFF, 0x7EBD0DFF, 0xA5CB1AFF, 0xCFD521FF, 0xF7E10FFF,
0xFFF39BFF, 0x692B0EFF, 0x89370FFF, 0xA24417FF, 0xBF4C12FF, 0xD85718FF, 0xF26420FF, 0xFC7C45FF,
0xF99A72FF, 0x5E121EFF, 0x821323FF, 0x9C192AFF, 0xBA1A31FF, 0xDA1939FF, 0xF52246FF, 0xFF284DFF,
0xFF2F55FF, 0x4D0825FF, 0x6D0934FF, 0x880B42FF, 0xA30A4FFF, 0xBF015CFF, 0xDB146BFF, 0xFA1079FF,
0xFF488AFF, 0x391B4BFF, 0x502667FF, 0x632E7FFF, 0x753995FF, 0x8A41B0FF, 0x9D4CC7FF, 0xB354E4FF,
0xC860FCFF, 0x472864FF, 0x5F3482FF, 0x724099FF, 0x844CB5FF, 0x9A55CCFF, 0xAE61EBFF, 0xC26DFFFF,
0xD679FFFF, 0x47299BFF, 0x5D33C8FF, 0x6F3FE8FF, 0x8048FFFF, 0x8F4EFFFF, 0xA25BFFFF, 0xAD98FEFF,
0xB9AFFFFF, 0x8A006EFF, 0xB20089FF, 0xCF009EFF, 0xF000B5FF, 0xFF00CDFF, 0xFF00E5FF, 0xFF81D3FF,
0xFFA3DBFF, 0x165F93FF, 0x2774B6FF, 0x2B87D0FF, 0x2E9AEAFF, 0x39AAFFFF, 0x3CBEFFFF, 0x5DD0FFFF,
0x90E2FFFF, 0x003AADFF, 0x004BDBFF, 0x0058FFFF, 0x0065FFFF, 0x0074FFFF, 0x5F9AFEFF, 0x7CABFFFF,
0x95BFFFFF, 0x1A00A8FF, 0x2400E5FF, 0x2A22FFFF, 0x3346FFFF, 0x4362FFFF, 0x5578FFFF, 0x6A8CFFFF,
0x80A0FFFF, 0x180882FF, 0x4D00FFFF, 0x6000FFFF, 0x3128FEFF, 0x3B48FFFF, 0x4B63FDFF, 0x5E78FDFF,
0x738CFCFF, 0x186556FF, 0x177E6BFF, 0x05937CFF, 0x13A78EFF, 0x1CBBA0FF, 0x23CFB1FF, 0x29E3C3FF,
0x0FFAD5FF, 0x167468FF, 0x138E7EFF, 0x1DA290FF, 0x02B8A3FF, 0x11CCB4FF, 0x00E2C6FF, 0x00F8DAFF,
0x7EFFEBFF,

"buzzer240" based on downgraded-32
0x00000000, 0x000000FF, 0x111111FF, 0x222222FF, 0x333333FF, 0x444444FF, 0x555555FF, 0x666666FF,
0x777777FF, 0x888888FF, 0x999999FF, 0xAAAAAAFF, 0xBBBBBBFF, 0xCCCCCCFF, 0xDDDDDDFF, 0xEEEEEEFF,
0xFFFFFFFF, 0x4D0A29FF, 0x6D0C39FF, 0x880E47FF, 0xA40F54FF, 0xBF0B62FF, 0xDE0971FF, 0xFA1580FF,
0xFF4A91FF, 0x88002AFF, 0xB60035FF, 0xDC0042FF, 0xFF004FFF, 0xFF0057FF, 0xFF0065FF, 0xFF4D86FF,
0xFE7397FF, 0x752131FF, 0x972B42FF, 0xB52F4BFF, 0xD03759FF, 0xEE3F63FF, 0xFF4871FF, 0xFF4F7AFF,
0xFF7291FF, 0x7A3F25FF, 0x9B4D2AFF, 0xB55A33FF, 0xCE673AFF, 0xE77341FF, 0xEC8E67FF, 0xEDA88BFF,
0xECC1AFFF, 0x795814FF, 0x946C16FF, 0xAB7D0FFF, 0xBE8D18FF, 0xD69D09FF, 0xEBAE16FF, 0xFFB100FF,
0xFFD344FF, 0xAE003AFF, 0xDF004BFF, 0xFF005AFF, 0xFF0068FF, 0xFF0076FF, 0xFF5D93FF, 0xFE80A5FF,
0xFE9FB8FF, 0x860045FF, 0xB10057FF, 0xD50069FF, 0xF80079FF, 0xFF008EFF, 0xFF009DFF, 0xFE60A6FF,
0xFE83B3FF, 0x4B144BFF, 0x661C67FF, 0x7B2280FF, 0x952695FF, 0xAD2EACFF, 0xC730C7FF, 0xE037DEFF,
0xF93EFCFF, 0x381646FF, 0x511E62FF, 0x652576FF, 0x792C8FFF, 0x9035A6FF, 0xA63BC0FF, 0xBC42DDFF,
0xD14EF5FF, 0x20005EFF, 0x31008CFF, 0x4100B9FF, 0x5100E4FF, 0x6300FFFF, 0x7500FFFF, 0x8500FFFF,
0x7C5DFFFF, 0x2D2545FF, 0x3C3460FF, 0x4A3F78FF, 0x5B4B8EFF, 0x6959A5FF, 0x7965C0FF, 0x8B71D7FF,
0x9A80F0FF, 0x006A5EFF, 0x168173FF, 0x009785FF, 0x0FAB97FF, 0x00C1A9FF, 0x00D5BBFF, 0x00E9CDFF,
0x00FEE0FF, 0x116862FF, 0x0C8178FF, 0x17958AFF, 0x1FA99CFF, 0x00BFB4FF, 0x00D3C6FF, 0x00F6E6FF,
0x00FBF0FF, 0x175767FF, 0x206E82FF, 0x227F98FF, 0x2992ABFF, 0x2BA5C3FF, 0x3AB7DBFF, 0x5AC7E8FF,
0x89D5EFFF, 0x2E2D7EFF, 0x413C9FFF, 0x4A48C0FF, 0x5554E3FF, 0x6560FFFF, 0x706CFFFF, 0x8178FFFF,
0x9BA3FFFF, 0x16344EFF, 0x254666FF, 0x29557BFF, 0x356491FF, 0x3A75A8FF, 0x4585BFFF, 0x5294D7FF,
0x56A7EFFF, 0x1E1A4CFF, 0x2D266CFF, 0x3A2E89FF, 0x4837A8FF, 0x5640CAFF, 0x614CE7FF, 0x7155FFFF,
0x8160FFFF, 0x35002EFF, 0x570048FF, 0x72005DFF, 0x910074FF, 0xB1008DFF, 0xCE00A6FF, 0xEF00C0FF,
0xFF00DCFF, 0x3C0824FF, 0x5C0037FF, 0x750044FF, 0x8C0D52FF, 0xA70D65FF, 0xC30973FF, 0xE10082FF,
0xFB1196FF, 0x550929FF, 0x750A38FF, 0x910C46FF, 0xAE0B54FF, 0xCA0361FF, 0xE61571FF, 0xFF2680FF,
0xFF5791FF, 0x6C052FFF, 0x910741FF, 0xAD034EFF, 0xCA1158FF, 0xE91167FF, 0xFE2B75FF, 0xFE5A8BFF,
0xFF7B9DFF, 0x79231FFF, 0x9C2C2AFF, 0xBA332BFF, 0xD53D39FF, 0xF44239FF, 0xFF4B41FF, 0xFF6657FF,
0xFF8D7CFF, 0x744A00FF, 0x8E5D00FF, 0xA76B0DFF, 0xBC7C17FF, 0xD48C0BFF, 0xFF8400FF, 0xFFA400FF,
0xFFC05DFF, 0x6C6D00FF, 0x7D8700FF, 0x879D11FF, 0xA7AB00FF, 0xC5B809FF, 0xDEC716FF, 0xFFD622FF,
0xFEEC9EFF, 0x4C6E0CFF, 0x5A880DFF, 0x679C19FF, 0x77AF0FFF, 0x83C319FF, 0x93D600FF, 0x81F600FF,
0xB2FD4CFF, 0x006A00FF, 0x008500FF, 0x009C00FF, 0x00AA34FF, 0x00B551FF, 0x2FC36EFF, 0x64D08AFF,
0x8ADCADFF, 0x11493EFF, 0x105F51FF, 0x197261FF, 0x0A8571FF, 0x119780FF, 0x1AAB91FF, 0x00CCA3FF,
0x00D6B3FF, 0x0B3930FF, 0x074E42FF, 0x105F51FF, 0x0A725BFF, 0x00926CFF, 0x00A07AFF, 0x00AC89FF,
0x1ABA9EFF,
 */

public class PaletteExpander {
    private static final int[] BASE_PALETTE = new int[]{
            //0x15111BFF, 0xFFEDD4FF,
//            0x6E6550FF, 0x372B26FF, 0xC37C6BFF, 0xDD997EFF, 0x9A765EFF, 0xE1AD56FF,
//            0xC6B5A5FF, 0xE9B58CFF, 0xEFCBB3FF, 0xF7DFAAFF, 0xBBD18AFF, 0x355525FF, 0x557A41FF, 0x112D19FF,
//            0x45644FFF, 0x62966AFF, 0x86BB9AFF, 0x15452DFF, 0x396A76FF, 0x86A2B7FF, 0x92B3DBFF, 0x3D4186FF,
//            0x6672BFFF, 0x9A76BFFF, 0x925EA2FF, 0xC7A2CFFF, 0x553549FF, 0xA24D72FF, 0xC38E92FF, 0xE3A6BBFF,
////// resurrect, minus 4
////            0xFFFFFFFF, 0xFBB954FF, 0x0B5E65FF, 0x3E3546FF,
//            0xFB6B1DFF, 0xE83B3BFF, 0x831C5DFF, 0xC32454FF, 0xF04F78FF, 0xF68181FF, 0xFCA790FF, 0xE3C896FF,
//            0xAB947AFF, 0x966C6CFF, 0x625565FF, 0x0B8A8FFF, 0x1EBC73FF, 0x91DB69FF, 0xFBFF86FF, 0xCD683DFF,
//            0x9E4539FF, 0x7A3045FF, 0x6B3E75FF, 0x905EA9FF, 0xA874F0FF, 0xEA6DE0FF, 0x8FD3FFFF, 0x4D9BE6FF,
//            0x4D65B4FF, 0x484A77FF, 0x30E1B9FF, 0x8FF8E2FF,
////// downgraded-32, minus 5, plus a dark green, and then tweaked for balance in this case
//            0x00000000, 0x887D8DFF, 0xB8B4B2FF, 0xDCDAC9FF, 0xFFFFE0FF, 0x464969FF,
            0x7B334CFF, 0xA14D55FF, 0xC77369FF, 0xE3A084FF, 0xF2CB9BFF, 0xD37B86FF, 0xAF5D8BFF,
            0x804085FF, 0x64336BFF, 0x38185CFF, 0x5C486AFF,
            0x76E0CBFF, 0x89D9D9FF, 0x72B6CFFF, 0x7C6BA8FF, 0x4E6679FF, 0x44355DFF, 0x3D003DFF,
            0x621748FF, 0x942C4BFF, 0xC7424FFF, 0xE06B51FF, 0xF2A561FF, 0xFCF448FF, 0xB1F460FF, 0x80B878FF,
            0x658D78FF, 0x3C6C54FF,

    };
    private static final int limit = 256;
    private static float minDistance = Float.MAX_VALUE;
    private static final IntArray rgba = new IntArray(limit);
    private static final IntSet dupes = new IntSet(limit);

    public static void add(int color) {
        if(!dupes.add(color))
            System.out.printf("%08X is a duplicate!\n", color);
        rgba.add(color);
    }
    public static void main(String[] args) {
        add(0);

//        add(0x000000FF);
//        add(0x141414FF);
//        add(0xFFFFFFFF);
//        add(0x878787FF);
//        add(0xCCCCCCFF);
//        add(0x4F4F4FFF);
//        add(0xEEEEEEFF);
//        add(0x282828FF);
//        add(0x999999FF);
//        add(0x757575FF);
//        add(0xDDDDDDFF);
//        add(0x3B3B3BFF);
//        add(0xBBBBBBFF);
//        add(0x626262FF);
//        add(0xAAAAAAFF);

//        add(0x000000FF);
//        add(0x141414FF);
//        add(0x282828FF);
//        add(0x3B3B3BFF);
//        add(0x4F4F4FFF);
//        add(0x626262FF);
//        add(0x757575FF);
//        add(0x878787FF);
//        add(0x999999FF);
//        add(0xAAAAAAFF);
//        add(0xBBBBBBFF);
//        add(0xCCCCCCFF);
//        add(0xDDDDDDFF);
//        add(0xEEEEEEFF);
//        add(0xFFFFFFFF);

        add(0x000000FF);
        add(0x111111FF);
        add(0x222222FF);
        add(0x333333FF);
        add(0x444444FF);
        add(0x555555FF);
        add(0x666666FF);
        add(0x777777FF);
        add(0x888888FF);
        add(0x999999FF);
        add(0xAAAAAAFF);
        add(0xBBBBBBFF);
        add(0xCCCCCCFF);
        add(0xDDDDDDFF);
        add(0xEEEEEEFF);
        add(0xFFFFFFFF);

//        for (int i = 0; i < 30; i++) {
//            float hue = i / 30f + (1f / 60f);
//            float A = TrigTools.cos_(hue) * 0.025f + 0.5f;
//            float B = TrigTools.sin_(hue) * 0.025f + 0.5f;
//            float L = (TrigTools.cos_(hue) + TrigTools.sin_(hue)) * 0.03125f + 0.666f + ((Integer.reverse(i + 1) >> 12) + 0x4000) * 0x1p-24f;
//            add(ColorTools.toRGBA8888(ColorTools.limitToGamut(L, A, B)));
//            int hi = (int)(hue * 256f);
//            float max = ColorTools.maximizeSaturation(L, A, B, 1f);
////            int gamut = ColorTools.getRawGamutValue(light << 8 | hi);
////            float gA = TrigTools.cos_(hue) * gamut * 0x1p-8f + 0.5f;
////            float gB = TrigTools.sin_(hue) * gamut * 0x1p-8f + 0.5f;
//            float gA = ColorTools.channelA(max);
//            float gB = ColorTools.channelB(max);
//            add(ColorTools.toRGBA8888(ColorTools.limitToGamut(L, gA, gB)));
//            float edit = TrigTools.sin(hi) * 0.08f;
//            float gA1 = MathUtils.lerp(A, gA, 0.25f + edit);
//            float gB1 = MathUtils.lerp(B, gB, 0.25f + edit);
//            add(ColorTools.toRGBA8888(ColorTools.limitToGamut(L + 0.2f, gA1, gB1)));
//            add(ColorTools.toRGBA8888(ColorTools.limitToGamut(L, gA1, gB1)));
//            add(ColorTools.toRGBA8888(ColorTools.limitToGamut(L - 0.2f, gA1, gB1)));
//            float gA2 = MathUtils.lerp(A, gA, 0.6f + edit);
//            float gB2 = MathUtils.lerp(B, gB, 0.6f + edit);
//            add(ColorTools.toRGBA8888(ColorTools.limitToGamut(L + 0.15f, gA2, gB2)));
//            add(ColorTools.toRGBA8888(ColorTools.limitToGamut(L, gA2, gB2)));
//            add(ColorTools.toRGBA8888(ColorTools.limitToGamut(L - 0.15f, gA2, gB2)));
//        }
//        for (int i = 0; i < BASE_PALETTE.length; i++) {
//            float lab = ColorTools.fromRGBA8888(BASE_PALETTE[i]);
//            int dec = NumberUtils.floatToRawIntBits(lab);
//            int light = dec & 0xFF;
//            float L = MathUtils.lerp(light / 255f, 0.666f, 0.5f);
//            float A = ColorTools.channelA(lab);
//            float B = ColorTools.channelB(lab);
//            rgba.add(ColorTools.toRGBA8888(ColorTools.limitToGamut(L + 0.1f, A, B)));
//            rgba.add(ColorTools.toRGBA8888(ColorTools.limitToGamut(L - 0.15f, A, B)));
//            float hue = TrigTools.atan2_((dec >>> 8 & 0xFF) - 127.5f, (dec >>> 16 & 0xFF) - 127.5f);
//            int hi = (int)(hue * 256f);
//            int gamut = ColorTools.getRawGamutValue(light << 8 | hi);
//            float gA = TrigTools.cos_(hue) * gamut * 0x1p-8f + 0.5f;
//            float gB = TrigTools.sin_(hue) * gamut * 0x1p-8f + 0.5f;
//            rgba.add(ColorTools.toRGBA8888(ColorTools.limitToGamut(L, gA, gB)));
//            float edit = TrigTools.sin(hi) * 0.1f;
//            float gA1 = MathUtils.lerp(A, gA, 0.3125f + edit);
//            float gB1 = MathUtils.lerp(B, gB, 0.3125f + edit);
//            rgba.add(ColorTools.toRGBA8888(ColorTools.limitToGamut(L + 0.15f, gA1, gB1)));
//            rgba.add(ColorTools.toRGBA8888(ColorTools.limitToGamut(L, gA1, gB1)));
//            rgba.add(ColorTools.toRGBA8888(ColorTools.limitToGamut(L - 0.225f, gA1, gB1)));
//            float gA2 = MathUtils.lerp(A, gA, 0.625f + edit);
//            float gB2 = MathUtils.lerp(B, gB, 0.625f + edit);
//            rgba.add(ColorTools.toRGBA8888(ColorTools.limitToGamut(L + 0.075f, gA2, gB2)));
//            rgba.add(ColorTools.toRGBA8888(ColorTools.limitToGamut(L - 0.125f, gA2, gB2)));
//        }

        for (int i = 0; i < BASE_PALETTE.length; i++) {
            float lab = ColorTools.fromRGBA8888(BASE_PALETTE[i]);
            int dec = NumberUtils.floatToRawIntBits(lab);
            int light = dec & 0xFF;
            float L = MathUtils.lerp(light / 255f, 0.666f, 0.5f);
            float A = ColorTools.channelA(lab);
            float B = ColorTools.channelB(lab);
            float hue = TrigTools.atan2_((dec >>> 8 & 0xFF) - 127.5f, (dec >>> 16 & 0xFF) - 127.5f);
            int hi = (int)(hue * 256f);
            float cosHue = TrigTools.cos_(hue);
            float sinHue = TrigTools.sin_(hue);
            float gamut = ColorTools.getRawGamutValue(light << 8 | hi) * 0x1p-8f;
            float gA = Math.abs(cosHue * gamut);
            float gB = Math.abs(sinHue * gamut);
            float fractionA = (A - 0.5f) / gA;
            float fractionB = (B - 0.5f) / gB;
            for (int j = 0; j < 8; j++) {
                float L2 = L - 0.26f + 0.045f * j + 0.03f * (float) Math.sqrt(j);
                float gamut2 = ColorTools.getRawGamutValue(((int) (L2 * 65535.999f) & 0xFF00) | hi) * 0x1p-8f;
                float color = ColorTools.limitToGamut(L2, fractionA * gamut2 + 0.5f, fractionB * gamut2 + 0.5f);
//                System.out.printf("%1.4f, %1.4f, %1.4f\n", ColorTools.channelL(color), ColorTools.channelA(color), ColorTools.channelB(color));
                rgba.add(ColorTools.toRGBA8888(color));
            }
        }

        StringBuilder sb = new StringBuilder(12 * rgba.size + 35).append("{\n");
        for (int i = 0; i < rgba.size; i++) {
            StringKit.appendHex(sb.append("0x"), rgba.get(i)).append(", ");
            if(7 == (i & 7)) sb.append('\n');
        }
        System.out.println(sb.append('}'));
//
//        System.out.println();
//        for (int i = 0; i < labs.size; i++) {
//            float c = labs.get(i);
//            System.out.printf("I=%f, P=%f, T=%f, RGBA=%08X\n",
//                    ColorTools.channelL(c), ColorTools.channelA(c), ColorTools.channelB(c), ColorTools.toRGBA8888(c));
//        }
    }
}
