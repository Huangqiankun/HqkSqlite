package com.hqk.hqksqlite.tools;

import android.content.Context;
import android.util.Log;


import com.hqk.hqksqlite.update.bean.CreateVersion;
import com.hqk.hqksqlite.update.bean.UpdateDbXml;
import com.hqk.hqksqlite.update.bean.UpdateStep;

import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DomUtils {
    /**
     * 读取升级xml
     *
     * @param context
     * @return
     */
    public static UpdateDbXml readDbXml(Context context) {
        InputStream is = null;
        Document document = null;
        try {
            is = context.getAssets().open("updateXml.xml");
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.parse(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (document == null) {
            return null;
        }

        UpdateDbXml xml = new UpdateDbXml(document);

        return xml;
    }
    /**
     * 新表插入数据
     *
     * @param xml
     * @param lastVersion 上个版本
     * @param thisVersion 当前版本
     * @return
     */
    public static UpdateStep findStepByVersion(UpdateDbXml xml, String lastVersion, String thisVersion) {
        if (lastVersion == null || thisVersion == null) {
            return null;
        }
        // 更新脚本
        UpdateStep thisStep = null;
        if (xml == null) {
            return null;
        }
        List<UpdateStep> steps = xml.getUpdateSteps();
        if (steps == null || steps.size() == 0) {
            return null;
        }

        for (UpdateStep step : steps) {
            if (step.getVersionFrom() == null || step.getVersionTo() == null) {
            } else {
                // 升级来源以逗号分隔
                String[] lastVersionArray = step.getVersionFrom().split(",");
                if (lastVersionArray != null && lastVersionArray.length > 0) {
                    for (int i = 0; i < lastVersionArray.length; i++) {
                        // 有一个配到update节点即升级数据
                        if (lastVersion.equalsIgnoreCase(lastVersionArray[i]) && step.getVersionTo().equalsIgnoreCase(thisVersion)) {
                            thisStep = step;
                            break;
                        }
                    }
                }
            }
        }
        return thisStep;
    }

    /**
     * 解析出对应版本的建表脚本
     *
     * @return
     */
    public static CreateVersion findCreateByVersion(UpdateDbXml xml, String version) {
        CreateVersion cv = null;
        if (xml == null || version == null) {
            return cv;
        }
        List<CreateVersion> createVersions = xml.getCreateVersions();

        if (createVersions != null) {
            for (CreateVersion item : createVersions) {
                Log.i("david", "item=" + item.toString());
                // 如果表相同则要支持xml中逗号分隔
                String[] createVersion = item.getVersion().trim().split(",");
                for (int i = 0; i < createVersion.length; i++) {
                    if (createVersion[i].trim().equalsIgnoreCase(version)) {
                        cv = item;
                        break;
                    }
                }
            }
        }

        return cv;
    }

}
