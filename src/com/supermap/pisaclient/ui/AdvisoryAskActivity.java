package com.supermap.pisaclient.ui;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.supermap.pisaclient.R;
import com.supermap.pisaclient.adapter.AdvisoryTypeAdapter;
import com.supermap.pisaclient.biz.AdvUploadDao;
import com.supermap.pisaclient.biz.SubjectDao;
import com.supermap.pisaclient.biz.UserDao;
import com.supermap.pisaclient.cache.ImageLoader;
import com.supermap.pisaclient.common.CommonDialog;
import com.supermap.pisaclient.common.CommonImageUtil;
import com.supermap.pisaclient.common.CommonUtil;
import com.supermap.pisaclient.common.Constant;
import com.supermap.pisaclient.common.Utils;
import com.supermap.pisaclient.common.views.CustomProgressDialog;
import com.supermap.pisaclient.common.views.PisaGridView;
import com.supermap.pisaclient.dao.CityDao;
import com.supermap.pisaclient.entity.AreaSelectParameter;
import com.supermap.pisaclient.entity.City;
import com.supermap.pisaclient.entity.ClientMsg;
import com.supermap.pisaclient.entity.Subject;
import com.supermap.pisaclient.entity.User;
import com.supermap.pisaclient.http.HttpHelper;
import com.supermap.pisaclient.ui.CropUploadActivity.DeleteImgOnLongClickListener;
import com.umeng.common.net.e;

public class AdvisoryAskActivity  extends BaseActivity implements OnItemClickListener{
	
	private View mContent;
	private EditText met_question;
	private ImageButton mbn_take_picture;
	private Button mbn_upload;
	private Button mbn_cancel;
	private PisaGridView mgv_question_type;
	private ImageView miv_picture_1;
	private ImageView miv_picture_2;
	private ImageView miv_picture_3;
	private LinearLayout mll_imgs;
	private TextView mtv_area;
	private TextView mtv_area_please;
	private TextView mtv_crop_please;
	private int mIndex = 0;
	private String FILE_PATH;
	private ImageView []miv_upload = new ImageView[3];
	private String[] mFilePath = new String[3];
	private Bitmap bmp;
	private boolean isEdit = false;
	private int mType;
	private List<Subject > mChildSubjects =  new ArrayList<Subject>();
	private String mAreaCode ;
	private String mQuestion = null;
	private int mAdvInfoId = 0;
	private int mSubjectId = 0;
	private CustomProgressDialog mPdDialog;
	private AdvUploadDao mAdvUploadDao;
	private AreaSelectParameter mAreaSelectParameter = null;
	private int mSelectTypePosition = -1;
	private List<String> mFilePathList = new ArrayList<String>();
	private int mDeleteIndex = -1;
	private List<Subject> mParentSubjectList = new ArrayList<Subject>();
	private HashMap<Integer, List<Subject>> mSubjectMap = new HashMap<Integer, List<Subject>>();
	private boolean isUploading = false;
	private User mUser;
	private CityDao mCityDao;
	private ImageLoader mLoader;
	
	
	private double lat;
	private double lng;

	public void initView(){
		met_question = (EditText) mContent.findViewById(R.id.et_advisroy_ask);
		mbn_upload = (Button) mContent.findViewById(R.id.bn_advisory_upload);
		mbn_cancel = (Button) mContent.findViewById(R.id.bn_advisory_cancel);
		mtv_area = (TextView) mContent.findViewById(R.id.tv_adv_area_select);
		mtv_area_please = (TextView) mContent.findViewById(R.id.tv_adv_area_remind);
		mtv_crop_please = (TextView) mContent.findViewById(R.id.tv_crop_please);
		mtv_area.setOnClickListener(this);
		mtv_area_please.setOnClickListener(this);
		mgv_question_type = (PisaGridView) mContent.findViewById(R.id.gv_advisory_ask_type);
		if (mType==10) {
			mgv_question_type.setVisibility(View.INVISIBLE);
		}
		miv_picture_1 = (ImageView) mContent.findViewById(R.id.iv_ask_1);
		miv_picture_2 = (ImageView) mContent.findViewById(R.id.iv_ask_2);
		miv_picture_3 = (ImageView) mContent.findViewById(R.id.iv_ask_3);
		miv_picture_1.setOnClickListener(this);
		miv_picture_2.setOnClickListener(this);
		miv_picture_3.setOnClickListener(this);
		miv_picture_1.setOnLongClickListener(new DeleteImgOnLongClickListener());
		miv_picture_2.setOnLongClickListener(new DeleteImgOnLongClickListener());
		miv_picture_3.setOnLongClickListener(new DeleteImgOnLongClickListener());
		miv_upload[0] = miv_picture_1;
		miv_upload[1] = miv_picture_2;
		miv_upload[2] = miv_picture_3;
		mll_imgs = (LinearLayout) mContent.findViewById(R.id.adv_img_list);
		mbn_upload.setOnClickListener(this);
		mbn_cancel.setOnClickListener(this);
		
	}
	
	private void initArea(){
		mUser = UserDao.getInstance().get();
		if (!"null".equals(mUser.areaCode.trim()) && !"".equals(mUser.areaCode.trim())) {
			String[] codes = mUser.areaCode.split(",");
			StringBuffer sb = new StringBuffer();
			for (String code : codes) {
				sb.append(mCityDao.queryCityName(code) + " ");
				mAreaCode = code;
				break;
			}
			String res = sb.toString();
			res = res.substring(0, res.length() - 1);
			mtv_area.setText(res);
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTvTitle(Utils.getString(this, R.string.advisory_ask));
		setIsBack(true);
		setIsNavigator(false);
		
		mChildSubjects  = (List<Subject>)getIntent().getSerializableExtra("subjects");
		
		mContent = inflater(R.layout.advisory_ask_main);
		mAdvUploadDao = new AdvUploadDao();
		mCityDao = new CityDao(this);
		initView();
		initArea();
		setBackOnClickListener(this);
		
		mPdDialog = CustomProgressDialog.createDialog(this);
		mPdDialog.setMessage(getResources().getString(R.string.upload_adv_data));
		new LoadSubjectTask().execute();
		mLoader = new ImageLoader(this);
	}
	
	class DeleteImgOnLongClickListener implements OnLongClickListener {

		@Override
		public boolean onLongClick(View view) {
			switch (view.getId()) {
			case R.id.iv_ask_1:
				mDeleteIndex = 0;
				break;
			case R.id.iv_ask_2:
				mDeleteIndex = 1;
				break;
			case R.id.iv_ask_3:
				mDeleteIndex = 2;
				break;
			default:
				break;
			}
			if (mDeleteIndex < mIndex) {
				showDeleteImgDia();
			}
			return false;
		}

	}
	
	private void upload(){
		if (UserDao.getInstance().get() == null) {
			CommonDialog.setLoginDialog(AdvisoryAskActivity.this);
			return;
		} 
		mQuestion = met_question.getText().toString();
		if(valid()){
			if (!isUploading) {
				new UploadAgrTask().execute();
				isUploading = true;
			}
			else {
				CommonUtil.showToask(AdvisoryAskActivity.this, "咨询上传中...");
			}
			
		}
	}
	
	private void cancel(){
		isEdit = false;
		met_question.setText("");
		mQuestion = null;
		
		mIndex = 0;
		setImg();
		mFilePathList.clear();
		
		mAreaCode = null;
		mtv_area.setText("");
		
		mSubjectId = 0;
		initSubjectSelectView();
		
		
	}
	
	
	private void selectArea(){
		Intent intent = new Intent(this, CitySetActivity.class);
		
		if (mAreaSelectParameter==null) {
			mAreaSelectParameter= new AreaSelectParameter();
			mAreaSelectParameter.flag = Constant.ADV_UPLOAD_REQUEST;
			mAreaSelectParameter.isWeatherArea = false;
			mAreaSelectParameter.isSelectMore = false;
			mAreaSelectParameter.isShowRemind = false;
		}
		Bundle bundle = new Bundle();
		bundle.putSerializable("parameter", mAreaSelectParameter);
		intent.putExtras(bundle);
		startActivityForResult(intent, Constant.ADV_UPLOAD_REQUEST);
	}
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.iv_ask_1:
		case R.id.iv_ask_2:
		case R.id.iv_ask_3:
			System.out.println("getpicture....");
			CommonImageUtil.getInstance(AdvisoryAskActivity.this).getPicture(mIndex + "");
			break;
		case R.id.ibtn_back:
			finish();
			break;
		case R.id.bn_advisory_upload:
			System.out.println("upload....");
			upload();
			break;
		case R.id.bn_advisory_cancel:
			cancel();
			break;
		case R.id.tv_adv_area_remind:
		case R.id.tv_adv_area_select:
			selectArea();
			break;

		default:
			break;
		}
		super.onClick(v);
	}
	
	
	
	private boolean valid() {
		if (("".equals(mQuestion))||(mQuestion==null)) {
			CommonUtil.showToask(this, getResources().getString(R.string.adv_please_info));
			return false;
		} else if ((mAreaCode==null)||("".equals(mAreaCode))) {
			CommonUtil.showToask(this, getResources().getString(R.string.suggest_please_area));
			return false;
		}
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 Bitmap bmp;
		 if ((resultCode ==RESULT_OK)&&(requestCode==Constant.IMAGE_CAPTURE_REQUEST)) {
			 
			 mll_imgs.setVisibility(View.VISIBLE);
			 FILE_PATH = CommonImageUtil.getInstance(this).getPicturePath();
			  if (FILE_PATH != null) {
					mFilePathList.add(FILE_PATH);
					mIndex++;
					isEdit = true;
					setImg();
				}
		 }
		 if ((resultCode ==RESULT_OK)&&(requestCode==Constant.IMAGE_GET_REQUEST)) {
			mll_imgs.setVisibility(View.VISIBLE);
			Uri uri = data.getData();
			uri = geturi(data);//解决方案  
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = managedQuery(uri, proj, null, null, null);
			//Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst(); 
  
			String path = cursor.getString(column_index);  
			if (path != null) {
				mFilePathList.add(path);
				mIndex++;
				isEdit = true;
				setImg();
			}
		 }
		 if ((resultCode ==Constant.CITY_SET_RESULT)&&(requestCode==Constant.ADV_UPLOAD_REQUEST)) {
			 List<City> list  = (List<City>)data.getSerializableExtra("city");
			if ((list != null)&&(list.size()>0)) {
				City city = list.get(0);
				mtv_area.setText(city.name);
				mAreaCode = city.areacode;
			}
		 }
	}
	
	public Uri geturi(android.content.Intent intent) {    
        Uri uri = intent.getData();    
        String type = intent.getType();    
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {    
            String path = uri.getEncodedPath();    
            if (path != null) {    
                path = Uri.decode(path);    
                ContentResolver cr = this.getContentResolver();    
                StringBuffer buff = new StringBuffer();    
                buff.append("(").append(Images.ImageColumns.DATA).append("=")    
                        .append("'" + path + "'").append(")");    
                Cursor cur = cr.query(Images.Media.EXTERNAL_CONTENT_URI,    
                        new String[] { Images.ImageColumns._ID },    
                        buff.toString(), null, null);    
                int index = 0;    
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {    
                    index = cur.getColumnIndex(Images.ImageColumns._ID);    
                    // set _id value    
                    index = cur.getInt(index);    
                }    
                if (index == 0) {    
                    // do nothing    
                } else {    
                    Uri uri_temp = Uri    
                            .parse("content://media/external/images/media/"    
                                    + index);    
                    if (uri_temp != null) {    
                        uri = uri_temp;    
                    }    
                }    
            }    
        }    
        return uri;    
    }    
	
	private void setImg() {

		for (int i = 1; i <= mIndex; i++) {// 设置图片
			bmp = CommonImageUtil.getInstance(AdvisoryAskActivity.this).decodeFile(mFilePathList.get(i - 1), 60, 60);
			bmp = CommonImageUtil.getInstance(AdvisoryAskActivity.this).cutBmp(bmp);
			miv_upload[i - 1].setImageBitmap(bmp);
			miv_upload[i - 1].setVisibility(View.VISIBLE);
		}
		if (mIndex < 3) {// 设置加图
			miv_upload[mIndex].setImageResource(R.drawable.adv_imge);
			miv_upload[mIndex].setVisibility(View.VISIBLE);
		}
		if (mIndex + 1 < 3) {// 隐藏view
			for (int i = mIndex + 1; i < 3; i++) {
				miv_upload[i].setVisibility(View.INVISIBLE);
			}
		}
	}

	private boolean deleteImag() {
		// mFilePath[mDeleteIndex] = null;
		mFilePathList.remove(mDeleteIndex);
		mIndex--;
		setImg();
		return true;
	}

	private void showDeleteImgDia() {
		new AlertDialog.Builder(AdvisoryAskActivity.this).setTitle("删除图片").setMessage("是否删除此图片?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						deleteImag();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();//
	}
	
	private class QuestionTypeAdapter extends BaseAdapter{
		
		private LayoutInflater mInflater;
		private Context mContext;
		private List<Subject> mDataList;
		
		public QuestionTypeAdapter(Context context,List<Subject> list){
			this.mContext = context;
			this.mDataList = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mChildSubjects.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			Subject tempSubject = mDataList.get(position);
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView =  mInflater.inflate(R.layout.advisory_question_type_item, null);
				holder.tvTypeName = (TextView) convertView.findViewById(R.id.tv_advisory_type_name);
				holder.ivTypeIcon = (ImageView) convertView.findViewById(R.id.iv_advisory_type_icon);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tvTypeName.setText(tempSubject.name);
			int r_id =-1;
			r_id = getAdvTypeDrawbleID(tempSubject.parentId, position);
			if (r_id!=-1) {
				holder.ivTypeIcon.setImageResource(r_id);	
			}
			else {
				holder.ivTypeIcon.setImageResource(R.drawable.sc_icon_takephoto_nor);
			}
			
			return convertView;
		}
		
		private class ViewHolder {
			public ImageView ivTypeIcon;
			public TextView tvTypeName;
		}
		
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		
		
		if(mSelectTypePosition!=-1){
			arg0.getChildAt(mSelectTypePosition).findViewById(R.id.ll_adv_type).setBackgroundColor(Color.WHITE);
			TextView tv2 = (TextView)(arg0.getChildAt(mSelectTypePosition).findViewById(R.id.tv_advisory_type_name));
			tv2.setTextColor(Color.parseColor("#4294fa"));
		}
		View itm = arg0.getChildAt(position);
		if(itm != null){
			itm.findViewById(R.id.ll_adv_type).setBackgroundResource(R.drawable.adv_ask_gvitem_back);
			TextView tv1 = (TextView)(arg0.getChildAt(position).findViewById(R.id.tv_advisory_type_name));
			tv1.setTextColor(Color.WHITE);
			CommonUtil.showToask(this,mChildSubjects.get(position).name);
			mSubjectId = mChildSubjects.get(position).subjectId;
			mSelectTypePosition = position;
		}
		
	}
	
	 @Override
		public void onBackPressed() {
			
			 if (isEdit) {
				 new AlertDialog.Builder(AdvisoryAskActivity.this) 
				    .setTitle("我要提问").setMessage("放弃此次编辑?")  
				    .setPositiveButton("确定",new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							AdvisoryAskActivity.this.finish();
						}
				    })  
				    .setNegativeButton("取消",new DialogInterface.OnClickListener() {  
				                @Override  
				                public void onClick(DialogInterface dialog,int which) { 
				                }  
				            }).show();// 
			}
			 else {
				 AdvisoryAskActivity.this.finish();
			}
		}
	 
	 /**
	  * to-do
	  * 
	  * 1.图片分三次上传，待优化减少http连接数目
	  * 2.上传失败以后，重新上传，不需要把所有信息再重复上传
	  * 
	  * @author kael
	  *
	  */
	 private class UploadAgrTask extends AsyncTask<String, Integer,Boolean>{
		    private boolean isReg = true;
			@Override
			protected void onPreExecute() {
				if (!mPdDialog.isShowing()){
					mPdDialog.show();
				}
			}
			@Override
			protected Boolean doInBackground(String... arg0) {
				File file = null;
				String imageURLContent = null;
				
				List<String> mImagPathList = new ArrayList<String>();
				for(String string :mFilePathList){
					if(string!=null){
						file = new File(string);
						//上传图片
						imageURLContent = HttpHelper.loadPic(mLoader.getPicInputStream(file), HttpHelper.ADD_PIC_URL);
						if (!imageURLContent.equals(HttpHelper.FAILURE)) {
							mImagPathList.add(imageURLContent);
						}
						else {
							return false;
						}
					}
				}
				//上传咨询信息
				mAdvInfoId = mAdvUploadDao.addAdvinfo(UserDao.getInstance().get().id, mAreaCode, mQuestion, mSubjectId);
				if (mAdvInfoId==0) {
					return false;
				}
				if (!mAdvUploadDao.addAdvImgs(mImagPathList,mAdvInfoId,mQuestion)) {
					mAdvUploadDao.addAdvImgs(mImagPathList,mAdvInfoId,mQuestion);
				}
				
				/*ClientMsg answerMsg = new ClientMsg();
				answerMsg.fromid = UserDao.getInstance().get().id;
				answerMsg.toid = UserDao.getInstance().getUserIdByExpertId(experid);
				answerMsg.msgtypeid = 3;
				answerMsg.mainid = commentid;
				answerMsg.msgsendtypeid = 1;
				answerMsg.content = "用户回复了我的专家建议";
				int msgid = messageUploadDao.addMsg(answerMsg);*/
				
				return true;
			
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (mPdDialog.isShowing()){
					mPdDialog.dismiss();
				}
				isUploading =false;
				if (result) {
					
					CommonUtil.showToask(AdvisoryAskActivity.this, "咨询信息上报成功 咨询ID为："+mAdvInfoId);
					AdvisoryAskActivity.this.finish();
				}
				else {
					CommonUtil.showToask(AdvisoryAskActivity.this, "咨询信息上报失败，请从新上传");
				}
			}
			
		}
	 
	 public  int getAdvTypeDrawbleID(int type,int position){
			int r_id = -1;
			
			String iconID = null;
			iconID = "advask"+2+""+position;
			
			Class drawable  =  R.drawable.class;
	        Field field = null;
	        try {
	            field = drawable.getField(iconID);
	            r_id = field.getInt(field.getName());
//	            iv.setBackgroundResource(r_id);
	        } catch (Exception e) {
	            return -1;
	        }
			return r_id;
		}
	 
	 private class LoadSubjectTask extends AsyncTask<String, Integer, List<Subject>> {
			@Override
			protected void onPreExecute() {

			}

			@Override
			protected List<Subject> doInBackground(String... arg0) {

				SubjectDao subjectDao = new SubjectDao();
				mChildSubjects = subjectDao.getSubjects(-1);
				return null;
			}

			@Override
			protected void onPostExecute(List<Subject> result) {
				initSubjectSelectView();
			}

		}
		
	 
	 public void initSubjectSelectView() {
		 
		  if ((mChildSubjects!=null)&&(mChildSubjects.size()>0)) {
			    mtv_crop_please.setVisibility(View.VISIBLE);
				mgv_question_type.setAdapter(new QuestionTypeAdapter(this,mChildSubjects));
				mgv_question_type.setOnItemClickListener(this);
				mgv_question_type.setSelector(new ColorDrawable(Color.TRANSPARENT));
			}
			
		}
}
