package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Oʻzbekcha — yordam ekranlari, `.claude/plans/help-screens.md`. */
internal val uzbekHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "Ilovaning asosiy ekrani: sayr shu yerda yoziladi. GPS orqali izingiz yoziladi, har bir topilma " +
            "esa koordinatalari va vaqti bilan saqlanadi — hammasi darrov xotiraga tushadi, shuning uchun " +
            "sayrni istalgan paytda toʻxtatsa boʻladi, yozilgani yoʻqolmaydi.",
    HelpKey.RecordStartFinish to
        "«Boshlash» nom soʻraydi va yozuvni boshlaydi; keyin tugma «Pauza»ga aylanadi, pauzada esa «Davom " +
            "etish» va «Tugatish» paydo boʻladi. «Tugatish» sayrni yopadi va uni «Sayrlar arxivi»ga " +
            "koʻchiradi.",
    HelpKey.RecordTiles to
        "Pastdagi qoʻziqorin kataklari topilmalarni belgilash uchun: «+» hozirgi nuqtangizda topilma " +
            "qoʻyadi, «−» shu turning oxirgi notoʻgʻri belgisini olib tashlaydi. «+» tugmasini uzoq " +
            "bosish bir nechtasini birdan kiritishni ochadi; bitta sayrda bir xil qoʻziqorindan 999 tadan " +
            "koʻpini belgilab boʻlmaydi.",
    HelpKey.RecordPlace to
        "Chapdagi dumaloq tugma joyni belgilaydi — nomi, tavsifi va surati bilan. Joy hozir turgan " +
            "nuqtangizga qoʻyiladi va sayrdan keyin ham xaritada qoladi.",
    HelpKey.RecordNavigation to
        "Joy belgisini uzoq bosish oʻsha yoqqa navigatsiyani yoqadi: oʻng yuqoridagi panel yoʻnalish va " +
            "manzilgacha masofani koʻrsatadi. Paneldagi krestcha navigatsiyani oʻchiradi.",
    HelpKey.RecordSearchAndOwn to
        "Oʻngdagi lupa qoʻziqorinni nomi boʻyicha topadi va uning katakchasini lentaning boshiga " +
            "oʻtkazadi — turlar koʻp yoqilgan boʻlsa, shu tezroq. Lentaning plyusli oxirgi katakchasi " +
            "katalogda yoʻq oʻz turingizni qoʻshadi.",
    HelpKey.RecordFilters to
        "Chap yuqoridagi «Filtrlar» tugmasi qaysi turlarning va qaysi davrning topilmalari xaritada " +
            "koʻrinishini belgilaydi, ustidagi raqam esa hozir nechta filtr yoqilganini bildiradi. Filtr " +
            "«Topilmalar xaritasi» bilan umumiy: bu yerda yoqqaningiz u yerda ham ishlaydi.",
    HelpKey.RecordBackground to
        "Izning yozilishi ilova fon rejimida boʻlganda ham davom etadi. Androidda davom " +
            "etayotgan sayr «+»/«−» tugmali bildirishnoma sifatida ham turadi — topilmani telefon " +
            "qulfini ochmasdan belgilash mumkin. Joriy sayrdan tashqari xarita oʻtgan sayrlarning " +
            "topilmalari va belgilangan joylarini koʻrsatadi — ulardan qayerda yurganingiz va u yerda " +
            "nima boʻlgani koʻrinadi.",
    HelpKey.ArchivePurpose to
        "Barcha sayrlaringiz, yangilari tepada. Kartochkada nomi, sanasi, davomiyligi, kilometrlari, " +
            "topilmalar soni va bosib oʻtilgan izning kichik tasviri bor.",
    HelpKey.ArchiveDetail to
        "Kartochkani bosish sayrni toʻliq ochadi: statistika, turlar boʻyicha topilmalar, belgilangan " +
            "joylar, tavsif va «Xaritani koʻrish» tugmasi. Nomi va tavsifini shu yerning oʻzida " +
            "oʻzgartirsa boʻladi.",
    HelpKey.ArchiveShare to
        "«Ulashish» tugmasi sayrning belgilagan qismlaridan rasm yigʻadi. Sayr xaritasini yuborishdan " +
            "oldin esda tuting: undan qoʻziqorinlarni aynan qayerdan topganingiz koʻrinadi.",
    HelpKey.ArchiveSelection to
        "Kartochkani uzoq bosish tanlash rejimini yoqadi: kerakli sayrlarni bosib belgilang va «Sayrlarni " +
            "oʻchirish» tugmasini bosing; «Orqaga» tugmasi bu rejimdan chiqadi. Oʻchirish qaytarilmaydi — " +
            "sayr bilan birga uning izi, topilmalari, belgilangan joylari va suratlari ham yoʻqoladi.",
    HelpKey.ArchiveUnfinished to
        "Tugallanmagan sayr ham roʻyxatda koʻrinadi: tugash vaqti oʻrniga unda «davom etmoqda» deb " +
            "yoziladi. Bunday sayrning hali davomiyligi yoʻq, shuning uchun «Topilmalar xaritasi»dagi " +
            "umumiy vaqtga kirmaydi.",
    HelpKey.MapPurpose to
        "Yigʻma xarita: barcha sayrlaringizning topilmalari, izlari va belgilangan joylari bitta yuzada " +
            "birdaniga. Umumiy manzarani koʻrish uchun kerak — qoʻziqorin joylaringiz qayerda va ular " +
            "yildan yilga qanday oʻzgaradi.",
    HelpKey.MapFullScreen to
        "Tepada barcha topilmalarni birdan koʻrsatadigan xarita; uni bosish xaritani butun ekranda " +
            "ochadi. Topilmalar koʻp boʻlsa, yaqin belgilar raqamli doiraga yigʻiladi — xaritani " +
            "yaqinlashtiring, u alohida qoʻziqorinlarga sochiladi. Qoʻziqorin belgilarining oʻlchami " +
            "«Sozlamalar»da sozlanadi.",
    HelpKey.MapSliders to
        "Xarita ostida ikkita surgich bor — sanalar oraligʻi va mavsum, yaʼni oylar oraligʻi — ulardan " +
            "pastdagi hamma narsa tanlanganga qarab hisoblanadi. Surgichlar faqat bir kundan koʻp kunda " +
            "sayringiz boʻlsa paydo boʻladi.",
    HelpKey.MapStats to
        "Surgichlar ostida: nechta sayr, kilometr, vaqt va topilma boʻlgani, turlar boʻyicha kataklar va " +
            "doiraviy diagramma. Umumiy vaqt tugallangan sayrlardan yigʻiladi: tugallanmaganining hali " +
            "davomiyligi yoʻq.",
    HelpKey.MapFilters to
        "«Filtrlar» tugmasi butun ekranli xaritada, chap yuqorida turadi: u yerda oʻsha ikki oʻq, turlar " +
            "roʻyxati va oʻtgan izlarni koʻrsatish tugmachasi bor. Tugmadagi raqam nechta filtr " +
            "yoqilganini aytadi; filtr yozuv ekrani bilan umumiy.",
    HelpKey.MapPlaces to
        "Joy belgisini bosish uning surat va tavsifli kartochkasini ochadi. Oʻsha yerdan joyni " +
            "oʻzgartirish yoki oʻchirish ham mumkin.",
    HelpKey.SpeciesPurpose to
        "Bu yerda yozuv ekranida qaysi qoʻziqorinlar katak boʻlishini hal qilasiz. Katalog mamlakatlar " +
            "boʻyicha toʻplamlarga boʻlingan, yonida esa katalogda yoʻq turlar yashaydi — ularni oʻzingiz " +
            "qoʻshasiz.",
    HelpKey.SpeciesCollections to
        "«Qoʻziqorin toʻplamlari»da mamlakat qatorini bosish uning turlarini ochadi: mamlakat " +
            "yonidagi belgi butun toʻplamni yoqadi, ichidagi belgilar — alohida turlarni. Yuqoridagi " +
            "qidiruv maydoni nom boʻyicha ham davlatni, ham alohida qoʻziqorinni topadi.",
    HelpKey.SpeciesOwn to
        "«Qoʻshilgan qoʻziqorinlar»da «Qoʻziqorin qoʻshish» tugmasi shaklni ochadi: nomi, ilmiy " +
            "nomi, belgi rangi va rasm — kameradan, galereyadan yoki katalogdan. Soʻng ilova «Qaysi " +
            "toʻplamga?» deb soʻraydi: oʻz nomingiz bunday qoʻziqorinlarni birga yigʻadi, boʻsh " +
            "maydon ularni «Boshqalar»ga qoʻyadi. Qalam qoʻshilgan turni oʻzgartiradi, krestcha uni " +
            "oʻchiradi.",
    HelpKey.SpeciesCheckboxes to
        "Olib tashlangan belgi hech narsani oʻchirmaydi — tur shunchaki katak boʻlib koʻrinmaydi, oʻtgan " +
            "topilmalar esa joyida qoladi. Oʻz turingizni oʻchirish esa qaytarilmaydi: uning oʻtgan " +
            "sayrlardagi barcha belgilari «Nomaʼlum qoʻziqorin»ga oʻtadi. «arxivdan» yozuvli turlar " +
            "import qilingan sayrlar bilan kelgan.",
    HelpKey.SpeciesImages to
        "Ilovadagi barcha qoʻziqorin rasmlari shartli: ular katakchani tanishga yordam beradi, oʻrmondagi " +
            "qoʻziqorinni emas. Ular boʻyicha notanish qoʻziqorinlarni aniqlamang.",
    HelpKey.PreparationPurpose to
        "Xarita boʻlaklarini oldindan telefon xotirasiga yuklaydi, shunda oʻrmonda internetsiz xarita " +
            "joyida qoladi: busiz aloqadan uzoqda xarita oʻrnida boʻsh fon boʻladi.",
    HelpKey.PreparationDownload to
        "Kerakli hududni toping — xaritani suring va masshtablang — soʻng pastki oʻng burchakdagi pastga " +
            "oʻq tushirilgan dumaloq tugmani bosing. Ilova hozir ekranda turgani qancha joy egallashini " +
            "koʻrsatadi: «Shu hududni yuklash» nom soʻraydi va yuklashni boshlaydi, «Bekor qilish» " +
            "xaritani qaytaradi.",
    HelpKey.PreparationRegions to
        "Yuklangan hududlar pastda tasma boʻlib turadi. Kartochkani bosish xaritada oʻsha hududga uchadi, " +
            "kartochkadagi tugmalar esa yuklashni pauzaga qoʻyadi va davom ettiradi, xatodan keyin " +
            "urinishni takrorlaydi va hududni oʻchiradi.",
    HelpKey.PreparationAreaSize to
        "Ekranda koʻringanning aynan oʻzi yuklanadi, shuning uchun xaritani surgan sari hajm bahosi " +
            "oʻzgaradi. Hudud qancha katta boʻlsa, uni shuncha kam tafsilotli qilishga toʻgʻri keladi — " +
            "bitta ulkan hudud oʻrniga bir nechta kichik uchastkani yuklash foydaliroq. Hudud nomlari " +
            "takrorlanmasligi kerak.",
    HelpKey.PreparationBackground to
        "Yuklash fonda ketadi va ekrandan chiqsangiz ham uzilmaydi, pauzada esa jarayon saqlanadi. " +
            "«Sozlamalar»dagi «Xarita maʼlumotlarini yangilash» saqlangan barcha hududni qaytadan " +
            "yuklaydi.",
    HelpKey.DataPurpose to
        "Sayrlarni telefonlar orasida koʻchirish va zaxira nusxa: tanlangan sayrlar bitta arxiv fayliga " +
            "yoziladi, bunday faylni esa qaytadan yuklash mumkin — shu yoki boshqa qurilmada.",
    HelpKey.DataExport to
        "Tepadagi tugmacha «Eksport» yoki «Import»ni tanlaydi. «Eksport»da arxivga nom bering, sayrlarni " +
            "tanlash qatorini bosib keraklilarini belgilang, soʻng «Tayyor» — telefon faylni qayerga " +
            "saqlashni soʻraydi.",
    HelpKey.DataImport to
        "«Import»da «Fayl tanlash» tugmasini bosing, xohlasangiz yuklanadigan sayrlar nomiga " +
            "qoʻshiladigan izoh yozing va «Tayyor» tugmasini bosing; arxiv oʻqilganda «Arxivga» tugmasi " +
            "paydo boʻladi. «Bekor qilish» tugmasi kiritilganni hech narsa saqlamay tozalaydi.",
    HelpKey.DataArchiveContents to
        "Arxivga iz, topilmalar, belgilangan joylar, suratlar va katalogda yoʻq qoʻziqorin turlari " +
            "tushadi — boshqa qurilmada ular «Qoʻshilgan qoʻziqorinlar»da «arxivdan» yozuvi bilan paydo " +
            "boʻladi.",
    HelpKey.DataDuplicates to
        "Import sayrlarni doim mavjudlarining yoniga qoʻshadi va hech narsani almashtirmaydi, shuning " +
            "uchun oʻsha faylni qayta yuklash ularni yana bir marta yaratadi: nomlarga qoʻshimcha keyin " +
            "ularni farqlashga yordam beradi. Oxirida nechta sayr yuklangani va nechtasi oʻqilmagani " +
            "koʻrsatiladi.",
    HelpKey.SettingsPurpose to
        "Ilovaning umumiy sozlamalari: interfeys tili, koʻrinishi, yozuv ekranidagi qoʻziqorin " +
            "kataklarining koʻrinishi va tartibi hamda xaritaga xizmat koʻrsatish.",
    HelpKey.SettingsLanguage to
        "«Interfeys tili» qatori tillar roʻyxatini ochadi: bosish tilni tanlaydi, tepadagi belgi tanlovni " +
            "tasdiqlaydi, oʻq hech narsani oʻzgartirmay chiqadi. Til butun ilovada darrov qoʻllanadi, " +
            "qayta ishga tushirish shart emas.",
    HelpKey.SettingsTheme to
        "«Koʻrinish» ilovaning yorugʻ va qorongʻi mavzusini almashtiradi. «Tizim» tanlovni telefonga " +
            "beradi: ilova u bilan birga qorayadi va yorishadi.",
    HelpKey.SettingsMushroomSize to
        "Surgich xaritadagi qoʻziqorin belgilarining oʻlchamini belgilaydi — yozuv ekranida ham, yigʻma " +
            "«Topilmalar xaritasi»da ham. Ostidagi rasm siz surayotganda oʻzgaradi, shuning uchun " +
            "oʻlchamni qoʻyib yubormasdan oldin koʻrasiz.",
    HelpKey.SettingsMushroomOrder to
        "Odatda endigina belgilangan qoʻziqorinlar katak lentasining boshiga koʻtariladi. «Qoʻziqorinlar " +
            "tartibini qotirish» buni butunlay oʻchiradi, «Sayr oxirida qoʻziqorinlar tartibini tiklash» " +
            "esa sayr tugagach dastlabki tartibni qaytaradi.",
    HelpKey.SettingsMapData to
        "«Xarita maʼlumotlarini yangilash» xarita serverda oʻzgargan-oʻzgarmaganini tekshiradi va " +
            "oʻzgargan boʻlsa, saqlangan barcha oflayn hududni qaytadan yuklaydi. «Xarita keshini " +
            "tozalash» faqat koʻrish vaqtida yuklanganni oʻchiradi — «Oldindan yuklash»dagi hududlar " +
            "joyida qoladi.",
)
