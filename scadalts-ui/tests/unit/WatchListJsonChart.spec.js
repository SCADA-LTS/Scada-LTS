import WatchListJsonChart from '@/components/watch_list/WatchListJsonChart';

import { expect } from 'chai';
import { prepareMountWrapper, LocalStorageMock } from '../utils/testing-utils';

const WATCHLIST_NAME = 'UnitTestWL';

/**
 * @private
 * Initialize VueWrapper for local testing
 * Prepare wrapper wiht all required stubs and props.
 */
function initWrapper() {
	return prepareMountWrapper(
		WatchListJsonChart,
		{},
		{
			pointId: '1',
			watchlistName: WATCHLIST_NAME,
			width: 600,
		},
		{ stubs: ['VApp'] },
	);
}

global.localStorage = new LocalStorageMock();

describe('WatchListJsonChart.vue Test', () => {
	const wrapper = initWrapper();

	it('Initialize blank JsonChart', () => {
		expect(wrapper.name()).to.equal('WatchListJsonChart');
		expect(wrapper.vm.pointId).to.equal('1');
		expect(wrapper.vm.chartType).to.equal('live');
	});

	it('Change to Static chart', () => {
		wrapper.find('#static-btn-1').trigger('click');
		expect(wrapper.vm.chartType).to.equal('static');
	});

	it('Change to Compare chart', () => {
		wrapper.find('#compare-btn-1').trigger('click');
		expect(wrapper.vm.chartType).to.equal('compare');
	});
});