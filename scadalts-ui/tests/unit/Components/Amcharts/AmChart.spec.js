import { expect } from "chai";
import sinon from "sinon";
import * as am4core from '@amcharts/amcharts4/core';
import AmChart, { AmChartBuilder, AmChart as BaseAmChart }  from "../../../../src/components/amcharts/AmChart"
import axios from "axios";
import { getValidDate } from '../../../../src/components/utils';


context('💠️ AmChart - base class Tests', () => {


    let chartInstance;

    before(() => {
        chartInstance = new AmChart("div", "xychart", "1,2");
    })
    describe('Class initialization', () => {
        it('Should be instance of AmChartBuilder', () => {
            expect(chartInstance).to.be.an.instanceOf(AmChartBuilder);
        });

        it('Is initialized "chartReference"', () => {
            expect(chartInstance.chartReference).to.equal('div');
        })

        it('Is initialized "chartType"', () => {
            expect(chartInstance.chartType).to.equal('xychart');
        })

        it('Is initialized "pointIds"', () => {
            expect(chartInstance.pointIds).to.equal('1,2');
        })

        it('Is initialized "colorPallete"', () => {
            expect(chartInstance.colorPallete).to.have.length(8);
        })

        it('Is initialized "xid"', () => {
            expect(chartInstance.isExportId).to.be.undefined;
            chartInstance.xid();
            expect(chartInstance.isExportId).to.be.true;
        })

        it('Is initialized "isStepLineChart"', () => {
            expect(chartInstance.isStepLineChart).to.be.undefined;
            chartInstance.stepLine();
            expect(chartInstance.isStepLineChart).to.be.true;
        })

        

        it('Is initialized "aggregation"', () => {
            expect(chartInstance.groupCount).to.be.undefined;
            chartInstance.useAggregation();
            expect(chartInstance.groupCount).to.equal(200);
            chartInstance.useAggregation(1000);
            expect(chartInstance.groupCount).to.equal(1000);
        })

        it('Is initialized "scrollbarX"', () => {
            expect(chartInstance.scrollbarX).to.be.undefined;
            chartInstance.showScrollbar();
            expect(chartInstance.scrollbarX).to.be.true;
        })

        it('Is initialized "legend"', () => {
            expect(chartInstance.legend).to.be.undefined;
            chartInstance.showLegend();
            expect(chartInstance.legend).to.be.true;
        })

        it('Is initialized "cursor"', () => {
            expect(chartInstance.cursor).to.be.undefined;
            chartInstance.showCursor();
            expect(chartInstance.cursor).to.be.true;
        })

        it('Is initialized "bullets"', () => {
            expect(chartInstance.bullets).to.be.undefined;
            chartInstance.showBullets();
            expect(chartInstance.bullets).to.be.true;
        })

        it('Is initialized "exportMenu"', () => {
            expect(chartInstance.exportMenu).to.be.undefined;
            chartInstance.showExportMenu();
            expect(chartInstance.exportMenu).to.equal("Scada_Chart");
            chartInstance.showExportMenu("CustomPrefix");
            expect(chartInstance.exportMenu).to.equal("CustomPrefix");
        })

        it('Is initialized "tension"', () => {
            expect(chartInstance.tension).to.be.undefined;
            chartInstance.smoothLine(12);
            expect(chartInstance.tension).to.be.undefined;
            chartInstance.smoothLine(-2);
            expect(chartInstance.tension).to.be.undefined;
            chartInstance.smoothLine();
            expect(chartInstance.tension).to.be.undefined;
            chartInstance.smoothLine(0.55);
            expect(chartInstance.tension).to.equal(0.55);
        })

        it('Is initialized "refreshRate"', () => {
            expect(chartInstance.refreshRate).to.be.undefined;
            chartInstance.withLiveUpdate();
            expect(chartInstance.refreshRate).to.be.undefined;
            chartInstance.withLiveUpdate(50000);
            expect(chartInstance.refreshRate).to.equal(50000);
        })

        it('Is initialized "startTimestamp"', () => {
            let testTime = new Date().getTime();
            expect(chartInstance.startTimestamp).to.be.undefined;
            chartInstance.startTime(testTime);
            expect(chartInstance.startTimestamp).to.equal(testTime);
        })

        it('Is initialized "endTimestamp"', () => {
            let testTime = new Date().getTime();
            expect(chartInstance.endTimestamp).to.be.undefined;
            chartInstance.endTime(testTime);
            expect(chartInstance.endTimestamp).to.equal(testTime);
        })

        it('Is Amchart created', () => {
            let amchart = chartInstance.build();
            expect(amchart).to.be.an.instanceOf(BaseAmChart);
        });
    })

    describe("Relative date tests - conversion", () => {

        it('Is "1-minute" converted', () => {
            let date = new Date().getTime() - 60000;
            let datePrecisionDown = date - 1;
            let datePrecisionUp = date + 1;
            expect(getValidDate("1-minute")).to.be.within(datePrecisionDown, datePrecisionUp);
        })

        it('Is "5-minutes" converted', () => {
            let date = new Date().getTime() - (60000 * 5);
            let datePrecisionDown = date - 1;
            let datePrecisionUp = date + 1;
            expect(getValidDate("5-minutes")).to.be.within(datePrecisionDown, datePrecisionUp);
        })

        it('Is "1-hour" converted', () => {
            let date = new Date().getTime() - (3600000);
            let datePrecisionDown = date - 1;
            let datePrecisionUp = date + 1;
            expect(getValidDate("1-hour")).to.be.within(datePrecisionDown, datePrecisionUp);
        })

        it('Is "5-hours" converted', () => {
            let date = new Date().getTime() - (3600000 * 5);
            let datePrecisionDown = date - 1;
            let datePrecisionUp = date + 1;
            expect(getValidDate("5-hours")).to.be.within(datePrecisionDown, datePrecisionUp);
        })

        it('Is "1-day" converted', () => {
            let date = new Date().getTime() - (3600000 * 24);
            let datePrecisionDown = date - 1;
            let datePrecisionUp = date + 1;
            expect(getValidDate("1-day")).to.be.within(datePrecisionDown, datePrecisionUp);
        })

        it('Is "5-days" converted', () => {
            let date = new Date().getTime() - (3600000 * 24 * 5);
            let datePrecisionDown = date - 1;
            let datePrecisionUp = date + 1;
            expect(getValidDate("5-days")).to.be.within(datePrecisionDown, datePrecisionUp);
        })

        it('Is "1-week" converted', () => {
            let date = new Date().getTime() - (3600000 * 24 * 7);
            let datePrecisionDown = date - 1;
            let datePrecisionUp = date + 1;
            expect(getValidDate("1-week")).to.be.within(datePrecisionDown, datePrecisionUp);
        })

        it('Is "2-weeks" converted', () => {
            let date = new Date().getTime() - (3600000 * 24 * 7 * 2);
            let datePrecisionDown = date - 1;
            let datePrecisionUp = date + 1;
            expect(getValidDate("2-weeks")).to.be.within(datePrecisionDown, datePrecisionUp);
        })

        it('Is "1-month" converted', () => {
            let date = new Date().getTime() - (3600000 * 24 * 7 * 4);
            let datePrecisionDown = date - 1;
            let datePrecisionUp = date + 1;
            expect(getValidDate("1-month")).to.be.within(datePrecisionDown, datePrecisionUp);
        })

        it('Is "5-months" converted', () => {
            let date = new Date().getTime() - (3600000 * 24 * 7 * 4 * 5);
            let datePrecisionDown = date - 1;
            let datePrecisionUp = date + 1;
            expect(getValidDate("5-months")).to.be.within(datePrecisionDown, datePrecisionUp);
        })

        it('Is "1-year" converted', () => {
            let date = new Date().getTime() - (3600000 * 24 * 7 * 4 * 12);
            let datePrecisionDown = date - 1;
            let datePrecisionUp = date + 1;
            expect(getValidDate("1-year")).to.be.within(datePrecisionDown, datePrecisionUp);
        })

        it('Is "5-years" converted', () => {
            let date = new Date().getTime() - (3600000 * 24 * 7 * 4 * 12 * 5);
            let datePrecisionDown = date - 1;
            let datePrecisionUp = date + 1;
            expect(getValidDate("5-years")).to.be.within(datePrecisionDown, datePrecisionUp);
        })

        it('Is on "1-day-2" Error generated', ()=> {
            expect(() => {getValidDate("1-day-2")}).to.throw();
        })

        it('Is on "1 day" Error generated', ()=> {
            expect(() => getValidDate("1 day")).to.throw();
        })

        it('Is "2020-06-24" converted', () => {
            let date = new Date("2020-06-24").getTime();
            expect(getValidDate("2020-06-24")).to.equal(date);
        })

        it('Is Date object converted', () => {
            let date = new Date();
            expect(getValidDate(date)).to.equal(date.getTime());
        })

        it('Is Date getTime number passed', () => {
            let date = new Date().getTime();
            expect(getValidDate(date)).to.equal(date);
        })
    })

    describe("AmChart behavoiur", () => {
        let sandbox;

        beforeEach(() => sandbox = sinon.createSandbox());
        afterEach(() => sandbox.restore());

        it("Test 'createChart' method", () => {
            let chart = chartInstance.build();

            let create = sinon.stub(chart, 'createChart');

            chart.createChart();

            sinon.assert.calledOnce(create);
        })

        it("Test 'fetchDataPointDetails' method", async() => {
            const response = {
                'id': 1,
                'xid': 'XID_01',
                'name': 'Example DP Details'
            }

            const resolved = new Promise((r) => r({data: response}));
            let get = sandbox.stub(axios, 'get').returns(resolved)
            
            let chart = chartInstance.build();
            let pointDetails = await chart.fetchDataPointDetails(1)

            expect(pointDetails).to.equal(response);
            sinon.assert.calledWith(get, `./api/point_value/getValue/1`);
        })

        it("Test 'fetchDataPointDetails' method with Error", async() => {
            sandbox.stub(axios, 'get').rejects(new Error("Failed to connect"));
            sandbox.stub(console, "error")
            
            
            let chart = chartInstance.build();

            try {
                await chart.fetchDataPointDetails(1)   
            } catch (e) {
                expect(e.message).to.match(/Failed to load Data Point details/);
            }
        })

        it("Test 'fetchPointValues' method with defined Timestamps", async() => {
            const response = [
                {
                    'date': 1624528984566,
                    '1': 12.3,
                    '2': 1.345,
                },
                {
                    'date': 1624528987566,
                    '1': 15.178,
                }
            ];

            const resolved = new Promise((r) => r({data: response}));
            let get = sandbox.stub(axios, 'get').returns(resolved)
            
            let chart = chartInstance.build();
            let startTs = new Date("2020-06-23").getTime();
            let endTs = new Date("2020-06-24").getTime();

            let requestUrl = `./api/amcharts/by-xid?startTs=${startTs}&endTs=${endTs}&ids=1,2`;

            let pointValues = await chart.fetchPointValues(startTs, endTs)

            expect(pointValues).to.equal(response);
            sinon.assert.calledWithMatch(get, requestUrl);
        })

        it("Test 'fetchPointValues' method with Start date greater then End Date", async() => {            
            let chart = chartInstance.build();
            let startTs = new Date("2020-06-26").getTime();
            let endTs = new Date("2020-06-24").getTime();

            try {
                await chart.fetchPointValues(startTs, endTs)
            } catch (e) {
                expect(e.message).to.match(/Start date is greater than End date!/);
            }
        })

    })

})