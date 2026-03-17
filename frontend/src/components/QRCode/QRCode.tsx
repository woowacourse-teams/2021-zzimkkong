import { QRCodeCanvas } from 'qrcode.react';
import { useCallback, useRef } from 'react';
import Button from 'components/Button/Button';
import { HREF } from 'constants/path';
import * as Styled from './QRCode.styles';

interface QRCodeProps {
  sharingSpaceId: string;
  spaceName: string;
  description?: string;
  size?: number;
}

const QRCode = ({
  sharingSpaceId,
  spaceName,
  description,
  size = 200,
}: QRCodeProps): JSX.Element => {
  const canvasRef = useRef<HTMLDivElement>(null);

  const basePath = HREF.SPACE_ENTRY(sharingSpaceId);
  const query = description ? `?description=${encodeURIComponent(description)}` : '';
  const entryUrl = `${window.location.origin}${basePath}${query}`;

  const handleDownload = useCallback(() => {
    const canvas = canvasRef.current?.querySelector('canvas');
    if (!canvas) return;

    const dataUrl = canvas.toDataURL('image/png');
    const link = document.createElement('a');
    link.download = `qr-${spaceName}.png`;
    link.href = dataUrl;
    link.click();
  }, [spaceName]);

  const handleCopyUrl = useCallback(async () => {
    try {
      await navigator.clipboard.writeText(entryUrl);
      alert('엔트리 URL이 클립보드에 복사되었습니다.');
    } catch {
      alert('URL 복사에 실패했습니다.');
    }
  }, [entryUrl]);

  const handlePrint = useCallback(() => {
    const canvas = canvasRef.current?.querySelector('canvas');
    if (!canvas) return;

    const dataUrl = canvas.toDataURL('image/png');

    const iframe = document.createElement('iframe');
    iframe.style.position = 'fixed';
    iframe.style.top = '-10000px';
    iframe.style.left = '-10000px';
    iframe.style.width = '0';
    iframe.style.height = '0';
    document.body.appendChild(iframe);

    const doc = iframe.contentDocument ?? iframe.contentWindow?.document;
    if (!doc) {
      document.body.removeChild(iframe);
      return;
    }

    doc.open();
    doc.write(`
      <html>
        <head>
          <style>
            @page { margin: 10mm; }
            body {
              display: flex;
              flex-direction: column;
              align-items: center;
              justify-content: center;
              min-height: 100vh;
              margin: 0;
              font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
            }
            img {
              width: 100%;
              max-width: 100vw;
              height: auto;
              margin-bottom: 0;
            }
            h2 { font-size: 2rem; margin: 0 0 0.5rem; }
            p { color: #666; font-size: 1.25rem; margin: 0; }
          </style>
        </head>
        <body>
          <img src="${dataUrl}" />
          <h2>${spaceName}</h2>
          ${description ? `<p>${description}</p>` : ''}
        </body>
      </html>
    `);
    doc.close();

    setTimeout(() => {
      iframe.contentWindow?.focus();
      iframe.contentWindow?.print();
      document.body.removeChild(iframe);
    }, 100);
  }, [spaceName, description]);

  return (
    <Styled.Container>
      <Styled.QRWrapper ref={canvasRef}>
        <QRCodeCanvas value={entryUrl} size={size} level="M" includeMargin />
      </Styled.QRWrapper>
      <Styled.SpaceName>{spaceName}</Styled.SpaceName>
      <Styled.ButtonWrapper>
        <Button variant="primary" size="small" onClick={handleDownload}>
          다운로드
        </Button>
        <Button variant="inverse" size="small" onClick={handleCopyUrl}>
          URL 복사
        </Button>
        <Button variant="inverse" size="small" onClick={handlePrint}>
          프린트
        </Button>
      </Styled.ButtonWrapper>
    </Styled.Container>
  );
};

export default QRCode;
