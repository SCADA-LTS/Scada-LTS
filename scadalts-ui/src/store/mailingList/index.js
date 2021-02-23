const storeMailingList = {
	state: {
		
	},

	mutations: {},

	actions: {
        getSimpleMailingLists({dispatch}) {
            console.debug("Simple Mailing List");
            // return dispatch('requestGet', '/mailinglist/simple/');
            return new Promise(resolve => {
                let data = [
                    {id: 1, xid: 'ML_00001', name: 'Example Mailing List'},
                    {id: 2, xid: 'ML_00002', name: 'Example ML Test'},
                    {id: 3, xid: 'ML_00005', name: 'Test Mailing List'},
                    {id: 4, xid: 'ML_00010', name: 'Admin Mailing List'},
                    {id: 5, xid: 'ML_00103', name: 'Fortnite ML'},
                    {id: 6, xid: 'ML_00424', name: 'Valanertis'},
                    {id: 7, xid: 'ML_00056', name: 'TestEr List'},
                    {id: 8, xid: 'ML_00012', name: 'DDRE List'}
                ];
                console.log(data);
                resolve(data);
            })
        },

        getMailingList({dispatch}, mailingListId) {
            // return dispatch('requestGet', '/mailinglist/{mailingListId}/');
            return new Promise(resolve => {
                let data = [
                    {
                        "id": 1,
                        "xid": "ML_00001",
                        "name": "Example Mailing List",
                        "entries": [
                          {
                            "userId": 1,
                            "user": {
                              "id": 1,
                              "username": "admin",
                              "email": "admin@yourMangoDomain.com",
                              "phone": "",
                              "admin": true,
                            },
                            "recipientType": 2,
                            "referenceAddress": null,
                            "referenceId": 1
                          }
                        ],
                        "cronPattern": "",
                        "collectInactiveEmails": false,
                        "dailyLimitSentEmailsNumber": 0,
                        "dailyLimitSentEmails": false,
                        "inactiveIntervals": [
                          0,
                          1,
                          2,
                          3,
                          4,
                          5,
                          6,
                          7,
                          8,
                          9,
                          10,
                          11,
                          12,
                          13,
                          16,
                          17,
                          576,
                          577,
                          578,
                          579
                        ],
                        "recipientType": 1,
                        "referenceAddress": null,
                        "referenceId": 1
                      }, {
                        "id": 2,
                        "xid": "ML_657273",
                        "name": "Example ML Test",
                        "entries": [
                          {
                            "address": "ersfasef",
                            "recipientType": 3,
                            "referenceAddress": "ersfasef",
                            "referenceId": 0
                          }
                        ],
                        "cronPattern": "",
                        "collectInactiveEmails": false,
                        "dailyLimitSentEmailsNumber": 0,
                        "dailyLimitSentEmails": false,
                        "inactiveIntervals": [],
                        "recipientType": 1,
                        "referenceAddress": null,
                        "referenceId": 2
                      }];

                if(mailingListId === 2) {
                    resolve(data[1])
                } else {
                    resolve(data[0])
                }
            });
        }
	},

	getters: {},
};

export default storeMailingList;
