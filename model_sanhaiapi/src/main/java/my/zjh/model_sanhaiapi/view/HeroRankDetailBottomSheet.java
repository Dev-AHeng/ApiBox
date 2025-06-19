package my.zjh.model_sanhaiapi.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

import my.zjh.model_sanhaiapi.R;
import my.zjh.model_sanhaiapi.adapter.HeroRankAdapter;
import my.zjh.model_sanhaiapi.model.HeroRankDetailResponse;
import my.zjh.model_sanhaiapi.viewmodel.HeroRankViewModel;

/**
 * 英雄战力排名详情底部弹出框
 */
public class HeroRankDetailBottomSheet extends BottomSheetDialogFragment {
    
    private HeroRankViewModel viewModel;
    
    // 视图绑定
    private ImageView imageHeroDetail;
    private TextView textHeroNameDetail;
    private TextView textHeroTitleDetail;
    private Chip chipHeroTypeDetail;
    
    private TextView textSystem;
    private TextView textPlatform;
    private TextView textQueryType;
    private TextView textSyncDate;
    
    private TextView textProvinceAddress;
    private TextView textProvinceRank;
    private RecyclerView recyclerProvinceRank;
    
    private TextView textCityAddress;
    private TextView textCityRank;
    private RecyclerView recyclerCityRank;
    
    private TextView textDistrictAddress;
    private TextView textDistrictRank;
    private RecyclerView recyclerDistrictRank;
    
    // 适配器
    private HeroRankAdapter provinceAdapter;
    private HeroRankAdapter cityAdapter;
    private HeroRankAdapter districtAdapter;
    
    // 参数
    private String heroName;
    private String zone;
    private String type;
    
    /**
     * 创建实例
     */
    public static HeroRankDetailBottomSheet newInstance(String heroName, String zone, String type) {
        HeroRankDetailBottomSheet fragment = new HeroRankDetailBottomSheet();
        Bundle args = new Bundle();
        args.putString("heroName", heroName);
        args.putString("zone", zone);
        args.putString("type", type);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 获取参数
        if (getArguments() != null) {
            heroName = getArguments().getString("heroName");
            zone = getArguments().getString("zone");
            type = getArguments().getString("type");
        }
        
        // 初始化ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(HeroRankViewModel.class);
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sh_bottom_sheet_hero_detail, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 初始化视图
        initViews(view);
        
        // 初始化适配器
        initAdapters();
        
        // 设置观察者
        observeViewModel();
        
        // 加载数据
        loadData();
    }
    
    /**
     * 初始化视图
     */
    private void initViews(View view) {
        // 英雄基本信息
        imageHeroDetail = view.findViewById(R.id.image_hero_detail);
        textHeroNameDetail = view.findViewById(R.id.text_hero_name_detail);
        textHeroTitleDetail = view.findViewById(R.id.text_hero_title_detail);
        chipHeroTypeDetail = view.findViewById(R.id.chip_hero_type_detail);
        
        // 游戏区域信息
        textSystem = view.findViewById(R.id.text_system);
        textPlatform = view.findViewById(R.id.text_platform);
        textQueryType = view.findViewById(R.id.text_query_type);
        textSyncDate = view.findViewById(R.id.text_sync_date);
        
        // 省级数据
        textProvinceAddress = view.findViewById(R.id.text_province_address);
        textProvinceRank = view.findViewById(R.id.text_province_rank);
        recyclerProvinceRank = view.findViewById(R.id.recycler_province_rank);
        
        // 市级数据
        textCityAddress = view.findViewById(R.id.text_city_address);
        textCityRank = view.findViewById(R.id.text_city_rank);
        recyclerCityRank = view.findViewById(R.id.recycler_city_rank);
        
        // 区级数据
        textDistrictAddress = view.findViewById(R.id.text_district_address);
        textDistrictRank = view.findViewById(R.id.text_district_rank);
        recyclerDistrictRank = view.findViewById(R.id.recycler_district_rank);
    }
    
    /**
     * 初始化适配器
     */
    private void initAdapters() {
        // 省级数据适配器
        provinceAdapter = new HeroRankAdapter(new ArrayList<>());
        recyclerProvinceRank.setAdapter(provinceAdapter);
        
        // 市级数据适配器
        cityAdapter = new HeroRankAdapter(new ArrayList<>());
        recyclerCityRank.setAdapter(cityAdapter);
        
        // 区级数据适配器
        districtAdapter = new HeroRankAdapter(new ArrayList<>());
        recyclerDistrictRank.setAdapter(districtAdapter);
    }
    
    /**
     * 观察ViewModel
     */
    private void observeViewModel() {
        // 观察英雄战力排名详情数据
        viewModel.getHeroRankDetail().observe(getViewLifecycleOwner(), this::updateUI);
        
        // 观察加载状态
        viewModel.getDetailLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // 可以添加加载指示器
        });
        
        // 观察错误信息
        viewModel.getDetailError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * 加载数据
     */
    private void loadData() {
        // 请求英雄战力排名详情数据
        viewModel.fetchHeroRankDetail(heroName, zone, type);
    }
    
    /**
     * 更新UI
     */
    private void updateUI(HeroRankDetailResponse response) {
        if (response == null || response.getData() == null) {
            return;
        }
        
        // 英雄基本信息
        HeroRankDetailResponse.Data data = response.getData();
        HeroRankDetailResponse.Hero hero = data.getHero();
        
        // 设置英雄头像
        if (response.getAvatar() != null && !response.getAvatar().isEmpty()) {
            Glide.with(this)
                    .load(response.getAvatar())
                    .error(R.drawable.sh_chip_skin_background)
                    .into(imageHeroDetail);
        }
        
        // 设置英雄名称和称号
        if (hero != null) {
            textHeroNameDetail.setText(hero.getName());
            textHeroTitleDetail.setText(hero.getTitle());
            chipHeroTypeDetail.setText("战力排名");
        }
        
        // 设置游戏区域信息
        HeroRankDetailResponse.Zone zoneInfo = data.getZone();
        if (zoneInfo != null) {
            textSystem.setText(zoneInfo.getSystem());
            textPlatform.setText(zoneInfo.getPlatform());
        }
        
        // 设置查询类型和同步日期
        textQueryType.setText("查询类型：" + data.getType());
        textSyncDate.setText("数据同步时间：" + data.getSynDate());
        
        // 设置战力排名数据
        HeroRankDetailResponse.RankData rankData = data.getRankData();
        if (rankData != null) {
            // 设置最值数据
            HeroRankDetailResponse.Extreme extreme = rankData.getExtreme();
            if (extreme != null) {
                // 省级数据
                if (extreme.getProvince() != null) {
                    textProvinceAddress.setText(extreme.getProvince().getAddress());
                    textProvinceRank.setText(String.valueOf(extreme.getProvince().getRank()));
                }
                
                // 市级数据
                if (extreme.getCity() != null) {
                    textCityAddress.setText(extreme.getCity().getAddress());
                    textCityRank.setText(String.valueOf(extreme.getCity().getRank()));
                }
                
                // 区级数据
                if (extreme.getDistrict() != null) {
                    textDistrictAddress.setText(extreme.getDistrict().getAddress());
                    textDistrictRank.setText(String.valueOf(extreme.getDistrict().getRank()));
                }
            }
            
            // 设置相近战力数据
            HeroRankDetailResponse.Similar similar = rankData.getSimilar();
            if (similar != null) {
                // 省级数据
                if (similar.getProvince() != null && !similar.getProvince().isEmpty()) {
                    recyclerProvinceRank.setVisibility(View.VISIBLE);
                    provinceAdapter.updateData(similar.getProvince());
                } else {
                    recyclerProvinceRank.setVisibility(View.GONE);
                }
                
                // 市级数据
                if (similar.getCity() != null && !similar.getCity().isEmpty()) {
                    recyclerCityRank.setVisibility(View.VISIBLE);
                    cityAdapter.updateData(similar.getCity());
                } else {
                    recyclerCityRank.setVisibility(View.GONE);
                }
                
                // 区级数据
                if (similar.getDistrict() != null && !similar.getDistrict().isEmpty()) {
                    recyclerDistrictRank.setVisibility(View.VISIBLE);
                    districtAdapter.updateData(similar.getDistrict());
                } else {
                    recyclerDistrictRank.setVisibility(View.GONE);
                }
            }
        }
    }
} 