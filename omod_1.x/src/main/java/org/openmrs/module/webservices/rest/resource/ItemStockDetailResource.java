package org.openmrs.module.webservices.rest.resource;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.openhmis.commons.api.PagingInfo;
import org.openmrs.module.openhmis.commons.api.entity.IObjectDataService;
import org.openmrs.module.openhmis.inventory.api.IItemStockDetailDataService;
import org.openmrs.module.openhmis.inventory.api.IStockroomDataService;
import org.openmrs.module.openhmis.inventory.api.model.ItemStockDetail;
import org.openmrs.module.openhmis.inventory.api.model.Stockroom;
import org.openmrs.module.openhmis.inventory.web.ModuleRestConstants;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

@Resource(name = ModuleRestConstants.ITEM_STOCK_DETAIL_RESOURCE, supportedClass = ItemStockDetail.class,
        supportedOpenmrsVersions = { "1.9.*", "1.10.*", "1.11.*", "1.12.*" })
public class ItemStockDetailResource extends ItemStockDetailBaseResource<ItemStockDetail> {
	
	private IStockroomDataService stockroomDataService;
	private IItemStockDetailDataService itemStockDetailDataService;
	
	public ItemStockDetailResource() {
		this.stockroomDataService = Context.getService(IStockroomDataService.class);
		this.itemStockDetailDataService = Context.getService(IItemStockDetailDataService.class);
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		description.addProperty("stockroom", Representation.DEFAULT);

		return description;
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		PageableResult result;
		String stockroomUuid = context.getParameter("stockroom_uuid");
		if (StringUtils.isNotBlank(stockroomUuid)) {
			PagingInfo pagingInfo = PagingUtil.getPagingInfoFromContext(context);
			Stockroom stockroom = stockroomDataService.getByUuid(stockroomUuid);
			List<ItemStockDetail> itemStockDetails =
			        itemStockDetailDataService.getItemStockDetailsByStockroom(stockroom, pagingInfo);
			result =
			        new AlreadyPagedWithLength<ItemStockDetail>(context, itemStockDetails, pagingInfo.hasMoreResults(),
			                pagingInfo.getTotalRecordCount());
		} else {
			result = super.doSearch(context);
		}
		return result;
	}
	
	@Override
	public PageableResult doGetAll(RequestContext context) {
		return doSearch(context);
	}
	
	@Override
	public ItemStockDetail newDelegate() {
		return new ItemStockDetail();
	}
	
	@Override
	public Class<? extends IObjectDataService<ItemStockDetail>> getServiceClass() {
		return IItemStockDetailDataService.class;
	}
}
