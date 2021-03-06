package com.lxd.ymxx.app;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.lxd.ymxx.utlis.ApiClient;
import com.lxd.ymxx.utlis.TitleBuilder;
import com.lxd.ymxx.utlis.ToastUtils;
import com.lxd.ymxx.utlis.Utlis;
import com.xinbo.utils.RegexValidateUtil;
import com.xinbo.utils.VolleyListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends Activity implements OnClickListener {

	private EditText edit_code;
	private EditText edit_phone;
	private EditText edit_pwd;
	private EditText edit_companyid;
	private Button btn_register;
	private Button btn_register_sendsms;
	private String phone;
	private String pwd;
	private String code;
	private String companyid;
	private boolean result = true;
	private int time = 60;
	private int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initui();
	}

	private void initui() {
		new TitleBuilder(this).setTitleText("注册").setLeftOnClickListener(this);
		edit_companyid = (EditText) findViewById(R.id.edit_companyid);
		edit_phone = (EditText) findViewById(R.id.edit_phone);
		edit_code = (EditText) findViewById(R.id.edit_code);
		edit_pwd = (EditText) findViewById(R.id.edit_pwd);
		btn_register = (Button) findViewById(R.id.btn_register);
		btn_register_sendsms = (Button) findViewById(R.id.btn_register_sendsms);
		btn_register.setOnClickListener(this);
		btn_register_sendsms.setOnClickListener(this);
		btn_register.setClickable(false);
		setTextChangedListener(edit_phone);
		setTextChangedListener(edit_companyid);
		setTextChangedListener(edit_pwd);
		setTextChangedListener(edit_code);
	}

	private void initdata() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", phone);
		params.put("pwd", pwd);
		params.put("companyid", companyid);
		params.put("code", code);
		// if ("".equals(phone)) {
		// ToastUtils.showToast(this, "您输入的手机号不能为空！");
		// return;
		// }
		// if ("".equals(pwd)) {
		// ToastUtils.showToast(this, "您输入的密码不能为空！");
		// return;
		//
		// }
		// if ("".equals(companyid)) {
		// ToastUtils.showToast(this, "请选择您需要绑定的商户！");
		// return;
		// }
		// if ("".equals(code)) {
		// ToastUtils.showToast(this, "您输入的验证码不能为空！");
		// return;
		// }

		// if (!RegexValidateUtil.checkMobileNumber(phone)) {
		// ToastUtils.showToast(this, "您输入的手机号有误，请重新输入！");
		// return;
		// }
		// if (!RegexValidateUtil.checkCharacter(pwd)) {
		// ToastUtils.showToast(this, "您输入的密码有误，请重新输入！");
		// return;
		// }
		ApiClient.getRegister(this, params, new VolleyListener() {
			@Override
			public void onResponse(String json) {
				json = Utlis.cutout(json);
				try {
					JSONObject jsonObject = new JSONObject(json);
					String code = jsonObject.getString("code");
					String msg = jsonObject.getString("msg");
					if ("1".equals(code)) {
						ToastUtils.showToast(RegisterActivity.this, "注册成功");
					} else {
						ToastUtils.showToast(RegisterActivity.this, msg);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onErrorResponse(VolleyError arg0) {

			}
		});
	}

	private void initcode() {
		phone = edit_phone.getText().toString();
		Map<String, String> params = new HashMap<>();
		params.put("phone", phone);
		if (!RegexValidateUtil.checkMobileNumber(phone)) {
			ToastUtils.showToast(this, "请输入正确的手机号！");
			return;
		}
		showTime();
		ApiClient.getsendsms(this, params, new VolleyListener() {
			@Override
			public void onResponse(String json) {
				json = Utlis.cutout(json);
				try {
					JSONObject jsonObject = new JSONObject(json);
					String code = jsonObject.getString("code");
					if ("1".equals(code)) {
						ToastUtils.showToast(RegisterActivity.this, "请稍等");
					} else {
						ToastUtils.showToast(RegisterActivity.this, "失败");
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onErrorResponse(VolleyError arg0) {

			}
		});
	}

	/**
	 * 显示时间在梯减的文本框
	 */
	public void showTime() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (result) {
					time--;
					try {
						Thread.sleep(1000);
						// tvShowTime.setText(time + "s后从新获取");
						btn_register_sendsms.post(new Runnable() {
							@Override
							public void run() {
								btn_register_sendsms.setText(time + "s后重新获取");
								btn_register_sendsms.setClickable(false);
								btn_register_sendsms
										.setBackgroundDrawable(getResources().getDrawable(R.drawable.rect_gray));
							}
						});
						if (time <= 1) {
							count = 0;
							result = false;
							btn_register_sendsms.post(new Runnable() {
								@Override
								public void run() {
									btn_register_sendsms.setText("获取验证码");
									btn_register_sendsms.setClickable(true);
									btn_register_sendsms
											.setBackgroundDrawable(getResources().getDrawable(R.drawable.rect_blue));
								}
							});
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				result = true;
				time = 60;
			}
		}).start();
	}

	private void setTextChangedListener(EditText editText) {
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				phone = edit_phone.getText().toString();
				pwd = edit_pwd.getText().toString();
				code = edit_code.getText().toString();
				companyid = edit_companyid.getText().toString();
				if (RegexValidateUtil.checkMobileNumber(phone) && RegexValidateUtil.checkCharacter(pwd)
						&& !"".equals(code) && !"".equals(companyid)) {
					btn_register.setBackgroundResource(R.drawable.rect_red);
					btn_register.setClickable(true);
					Log.e("true", "触发");
				} else {
					btn_register.setBackgroundResource(R.drawable.rect_gray);
					btn_register.setClickable(false);
					Log.e("false", "触发");
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_img_back:
			finish();
			break;
		case R.id.btn_register:// 注册
			initdata();
			break;
		case R.id.btn_register_sendsms:// 获取验证码
			initcode();
			break;

		default:
			break;
		}
	}

}
