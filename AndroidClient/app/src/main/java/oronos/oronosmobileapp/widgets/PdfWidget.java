package oronos.oronosmobileapp.widgets;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import java.io.InputStream;
import java.util.List;

import okhttp3.ResponseBody;
import oronos.oronosmobileapp.R;
import oronos.oronosmobileapp.httpClientUtilities.FileDownloadClient;
import oronos.oronosmobileapp.settings.Configuration;
import oronos.oronosmobileapp.utilities.FLog;
import oronos.oronosmobileapp.utilities.Tag;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
/**
 * PdfWidget.java
 * Fragment qui permet l'affichage et la gestion des PDF.
 */

public class PdfWidget extends Fragment implements OnPageChangeListener, OnLoadCompleteListener {

    private PDFView pdfView;
    private Integer pageNumber = 0;
    private String pdfFileName;
    private TextView pdfTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pdf_viewer, container, false);
        pdfView = (PDFView) v.findViewById(R.id.pdfView);
        pdfTitle = (TextView) v.findViewById(R.id.pdf_header);
        downloadFile();
        return v;
    }

    private void displayFromHttp(InputStream inputStream) {
        pdfView.fromStream(inputStream)
                .defaultPage(pageNumber)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(getContext()))
                .load();
    }

    public void setPdfFileName(String fileName) {
        pdfFileName = fileName;
    }

    public String getPdfFileName() {
        return pdfFileName;
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        pdfTitle.setText(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(), "-");

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            FLog.e(Tag.PDF, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    private void downloadFile() {
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(Configuration.getIpAddr());
        Retrofit retrofit = builder.build();

        FileDownloadClient fileDownloadClient = retrofit.create(FileDownloadClient.class);
        Call<ResponseBody> call = fileDownloadClient.downloadFile("/config/miscFiles/" + pdfFileName);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                displayFromHttp(response.body().byteStream());
                Toast.makeText(getContext(), "Success to download " + pdfFileName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Fail to download " + pdfFileName, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
