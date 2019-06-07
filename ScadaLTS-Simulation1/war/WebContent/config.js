var viewConfiguration = {
    mainTitle: "ScadaLTS View",
    navTitle: "Navigation",
    cardDescription: ["All day: ", "Enable/Disable", "Current Hour", "Last Hour", "Current Day"],
    logoSrc: "resources/logo.png",
    enableButtonDSxid: "DP_558690",
    configLocationDSxid: "DP_558691",
    /* weekEnableShedule [mon, tue, wed, th, fri, sat, sun] :: 0 is disable 1 is enable */
    weekEnableShedule: [0, 1, 1, 1, 1, 0, 0],
    /* weekEnableHours [minHour, maxHour] :: when time is beatween this values DS is enabled */
    weekEnableHours: [9, 17]
}