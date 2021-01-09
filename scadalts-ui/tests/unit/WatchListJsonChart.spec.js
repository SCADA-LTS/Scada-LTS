import { createLocalVue, mount } from '@vue/test-utils';
import WatchListJsonChart from '@/components/watch_list/WatchListJsonChart';
import i18n from '@/i18n';
import * as uiv from 'uiv';
import { expect } from 'chai';

class LocalStorageMock {
	constructor() {
		this.store = {};
	}

	clear() {
		this.store = {};
	}

	getItem(key) {
		return this.store[key] || null;
	}

	setItem(key, value) {
		this.store[key] = JSON.stringify(value);
	}

	removeItem(key) {
		delete this.store[key];
	}
}

global.localStorage = new LocalStorageMock();

const localVue = createLocalVue();
localVue.use(uiv);

describe('WatchListJsonChart.vue Test', () => {
	const WATCHLIST_NAME = 'UnitTestWL';

	const wrapper = mount(WatchListJsonChart, {
		localVue,
		i18n,
		propsData: {
			pointId: '1',
			watchlistName: WATCHLIST_NAME,
		},
	});

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
