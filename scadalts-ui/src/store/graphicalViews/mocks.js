const viewComponentsMock = [
    {
        index: 0,
        type: 'SIMPLE',
        dataPointXid: 'DP_858030',
        bkgdColorOverride: '',
        displayControls: false,
        displayPointName: false,
        nameOverride: '',
        settableOverride: true,
        styleAttribute: null,
        x: 286,
        y: 134,
        z: 2,
    },
    {
        index: 1,
        type: 'HTML',
        content: '<h1>Contern<br>Welcome Scada-LTS!</h1>',
        x: 489,
        y: 63,
        z: 2,
    },
    {
        index: 2,
        type: "BINARY_GRAPHIC",
        dataPointXid: "TQ_VDP_01_01",
        imageSet: "Scada-LTS-Fan",
        bkgdColorOverride: "",
        displayControls: true,
        displayText: true,
        nameOverride: "",
        oneImageIndex: 2,
        settableOverride: true,
        x: 63,
        y: 29,
        z: 2,
        zeroImageIndex: 1
    }
]

export const graphicalViewPage = {
    user: "admin",
    anonymousAccess: "NONE",
    viewComponents: viewComponentsMock,
    sharingUsers: [],
    resolution: "R1024x768",
    backgroundFilename: null,
    name: "Test",
    id: 1,
    xid: "GV_598801"
}

export const imageSets = [{
    value: "Scada-LTS-Fan",
    text: "Scada-LTS Fan"
}, {
    value: "Light-bulbs",
    text: "Light Bulbs"
}]

export const imageSetFan = {
    id: 1,
    name: "Scada-LTS-Fan",
    imageFilenames: [
        "http://localhost:8080/Scada-LTS/graphics/CarBattery/black.png",
        "http://localhost:8080/Scada-LTS/graphics/CarBattery/blue.png",
        "http://localhost:8080/Scada-LTS/graphics/CarBattery/green.png",
        "http://localhost:8080/Scada-LTS/graphics/CarBattery/purple.png",
        "http://localhost:8080/Scada-LTS/graphics/CarBattery/red.png",
        "http://localhost:8080/Scada-LTS/graphics/CarBattery/yellow.png",
    ],
    // imageFilenames: [
    //     "http://localhost:8080/Scada-LTS/graphics/Scada-LTS-Fan/slts_fan_disable.png",
    //     "http://localhost:8080/Scada-LTS/graphics/Scada-LTS-Fan/slts_fan_off.png",
    //     "http://localhost:8080/Scada-LTS/graphics/Scada-LTS-Fan/slts_fan_on.png"
    // ],
    width: 56,
    height: 56,
}

export const backgroundImages = [
    {
        name: "Firefighert",
        imgUrl: "http://localhost:8080/Scada-LTS/uploads/1.jpg",
        width: 1920,
        height: 1229
    },
    {
        name: "Loft",
        imgUrl: "http://localhost:8080/Scada-LTS/uploads/Loft.gif",
        width: 927,
        height: 399,
    }

]